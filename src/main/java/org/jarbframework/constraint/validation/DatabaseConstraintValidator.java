package org.jarbframework.constraint.validation;

import static org.jarbframework.utils.StringUtils.isNotBlank;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.MessageInterpolator;

import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.utils.bean.Beans;
import org.jarbframework.utils.bean.FlexibleBeanWrapper;
import org.jarbframework.utils.bean.PropertyReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates the property values of a bean satisfy our database constraints.
 * Performed database checks include:
 * 
 * <ul>
 *  <li>Not nullable columns cannot have {@code null} values, unless they are generated</li>
 *  <li>Column length can never be exceeded. (char and number based)</li>
 *  <li>Fraction length in number column cannot be exceeded</li>
 * </ul>
 * 
 * Whenever a database constraint is violated, a constraint violation will be
 * added for that property. Multiple constraint violations can occur, even on
 * the same property.
 * 
 * @author Jeroen van Schagen
 * @since 23-05-2011
 */
public class DatabaseConstraintValidator {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** Concrete validation logic that should be performed **/
    private final List<DatabaseConstraintValidationStep> validationSteps;

    /** Retrieves the column meta-data that we use for validation **/
    private final BeanMetadataRepository beanMetadataRepository;
    
    /** Used to build violation messages **/
    private final ViolationMessageBuilder messageBuilder;

    /**
     * Construct a new {@link DatabaseConstraintValidator}.
     * @param beanMetadataRepository beanMetadataRepository
     * @param messageInterpolator messageInterpolator
     */
    public DatabaseConstraintValidator(BeanMetadataRepository beanMetadataRepository, MessageInterpolator messageInterpolator) {
        validationSteps = new ArrayList<>();
        validationSteps.add(new NotNullConstraintValidationStep());
        validationSteps.add(new LengthConstraintValidationStep());
        validationSteps.add(new FractionLengthConstraintValidationStep());
        
        this.beanMetadataRepository = beanMetadataRepository;
        this.messageBuilder = new ViolationMessageBuilder(messageInterpolator);
    }

    /**
     * Determine if a bean follows all column constraints defined in the database.
     * 
     * @param bean the bean that should be validated
     * @param entityClass class of the entity
     * @param propertyName property to look for
     * @param validatorContext used to create constraint violations
     * @return whether the bean is valid, or not
     */
    public boolean isValid(Object bean, Class<?> entityClass, String propertyName, ConstraintValidatorContext validatorContext) {
        DatabaseValidationContext validation = new DatabaseValidationContext(validatorContext, messageBuilder);
        validateBean(bean, new BasePath(entityClass, propertyName), validation);
        return validation.isValid();
    }

    private void validateBean(Object bean, BasePath basePath, DatabaseValidationContext validation) {
        FlexibleBeanWrapper beanWrapper = new FlexibleBeanWrapper(bean);
        for (String propertyName : Beans.getFieldNames(bean.getClass())) {
            validateProperty(beanWrapper, new PropertyReference(bean.getClass(), propertyName), basePath, validation);
        }
    }

    private void validateProperty(FlexibleBeanWrapper beanWrapper, PropertyReference propertyReference, BasePath basePath, DatabaseValidationContext validation) {
        Field field = Beans.findPropertyField(propertyReference);
        if (isValidatable(field)) {
            Class<?> propertyClass = field.getType();
            if (beanMetadataRepository.isEmbeddable(propertyClass)) {
                for (String propertyName : Beans.getFieldNames(propertyClass)) {
                    validateProperty(beanWrapper, new PropertyReference(propertyReference, propertyName), basePath, validation);
                }
            } else {
                validateSimpleProperty(beanWrapper, propertyReference, basePath, validation);
            }
        }
    }

    private boolean isValidatable(Field field) {
        return !Modifier.isStatic(field.getModifiers()) && field.getAnnotation(IgnoreDatabaseConstraints.class) == null;
    }

    private void validateSimpleProperty(FlexibleBeanWrapper beanWrapper, PropertyReference propertyReference, BasePath basePath, DatabaseValidationContext validation) {
        PropertyReference wrappedPropertyReference = basePath.apply(propertyReference);
        ColumnMetadata columnMetadata = beanMetadataRepository.getColumnMetadata(wrappedPropertyReference);
        if (columnMetadata != null) {
            Object propertyValue = beanWrapper.getPropertyValue(propertyReference.getPropertyName());
            for (DatabaseConstraintValidationStep validationStep : validationSteps) {
                validationStep.validate(propertyValue, propertyReference, columnMetadata, validation);
            }
        } else {
            logger.debug("Skipped validation because no meta-data could be found for property '{}'.", wrappedPropertyReference);
        }
    }
    
    private static final class BasePath {
        
        private final Class<?> baseClass;
        
        private final String propertyName;
        
        BasePath(Class<?> baseClass, String propertyName) {
            this.baseClass = baseClass;
            this.propertyName = propertyName;
        }
        
        PropertyReference apply(PropertyReference propertyPath) {
            if (hasBaseClass()) {
                if (isNotBlank(propertyName)) {
                    // Include parent path to reference
                    PropertyReference parent = new PropertyReference(baseClass, propertyName);
                    return new PropertyReference(parent, propertyPath.getPropertyName());
                } else {
                    // Alter base bean type to our specified entity class
                    return new PropertyReference(baseClass, propertyPath.getPropertyName());
                }
            }
            return propertyPath;
        }

        private boolean hasBaseClass() {
            return !Object.class.equals(baseClass);
        }
        
    }

}
