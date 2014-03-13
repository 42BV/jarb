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
import org.jarbframework.utils.bean.BeanProperties;
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
    private BeanMetadataRepository beanMetadataRepository;
    
    /** Used to build violation messages **/
    private ViolationMessageBuilder messageBuilder;

    /**
     * Construct a new {@link DatabaseConstraintValidator}.
     */
    public DatabaseConstraintValidator(BeanMetadataRepository beanMetadataRepository, MessageInterpolator messageInterpolator) {
        validationSteps = new ArrayList<DatabaseConstraintValidationStep>();
        validationSteps.add(new NotNullConstraintValidationStep());
        validationSteps.add(new LengthConstraintValidationStep());
        validationSteps.add(new FractionLengthConstraintValidationStep());
        
        this.beanMetadataRepository = beanMetadataRepository;
        messageBuilder = new ViolationMessageBuilder(messageInterpolator);
    }

    /**
     * Determine if a bean follows all column constraints defined in the database.
     * @param bean the bean that should be validated
     * @param base the reference to our bean, or {@code null} if the bean is our root
     * @param validatorContext used to create constraint violations
     * @return whether the bean is valid, or not
     */
    public boolean isValid(Object bean, Class<?> entityClass, String propertyName, ConstraintValidatorContext validatorContext) {
        DatabaseValidationContext validation = new DatabaseValidationContext(validatorContext, messageBuilder);
        validateBean(bean, new RootPath(entityClass, propertyName), validation);
        return validation.isValid();
    }

    private void validateBean(Object bean, RootPath rootPath, DatabaseValidationContext validation) {
        FlexibleBeanWrapper<?> beanWrapper = FlexibleBeanWrapper.wrap(bean);
        for (String propertyName : BeanProperties.getFieldNames(bean.getClass())) {
            validateProperty(beanWrapper, new PropertyReference(bean.getClass(), propertyName), rootPath, validation);
        }
    }

    private void validateProperty(FlexibleBeanWrapper<?> beanWrapper, PropertyReference propertyPath, RootPath rootPath, DatabaseValidationContext validation) {
        Field propertyField = BeanProperties.findPropertyField(propertyPath);
        if (! Modifier.isStatic(propertyField.getModifiers())) {
            Class<?> propertyClass = propertyField.getType();
            if (beanMetadataRepository.isEmbeddable(propertyClass)) {
                for (String propertyName : BeanProperties.getFieldNames(propertyClass)) {
                    validateProperty(beanWrapper, new PropertyReference(propertyPath, propertyName), rootPath, validation);
                }
            } else {
                validateSimpleProperty(beanWrapper, propertyPath, rootPath, validation);
            }
        }
    }

    private void validateSimpleProperty(FlexibleBeanWrapper<?> beanWrapper, PropertyReference propertyPath, RootPath rootPath, DatabaseValidationContext validation) {
        PropertyReference wrappedPath = rootPath.wrap(propertyPath);
        ColumnMetadata columnMetadata = beanMetadataRepository.getColumnMetadata(wrappedPath);
        if (columnMetadata != null) {
            Object propertyValue = beanWrapper.getPropertyValueNullSafe(propertyPath.getName());
            for (DatabaseConstraintValidationStep validationStep : validationSteps) {
                validationStep.validate(propertyValue, propertyPath, columnMetadata, validation);
            }
        } else {
            logger.debug("Skipped validation because no meta-data could be found for property '{}'.", wrappedPath);
        }
    }
    
    public static class RootPath {
        
        private final Class<?> entityClass;
        
        private final String propertyName;
        
        public RootPath(Class<?> entityClass, String propertyName) {
            this.entityClass = entityClass;
            this.propertyName = propertyName;
        }
        
        public PropertyReference wrap(PropertyReference propertyPath) {
            if (!Object.class.equals(entityClass)) {
                if (isNotBlank(propertyName)) {
                    // Include parent path to reference
                    PropertyReference parent = new PropertyReference(entityClass, propertyName);
                    return new PropertyReference(parent, propertyPath.getName());
                } else {
                    // Alter base bean type to our specified entity class
                    return new PropertyReference(entityClass, propertyPath.getName());
                }
            }
            return propertyPath;
        }
        
    }

}
