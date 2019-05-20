package nl._42.jarb.constraint.validation;

import nl._42.jarb.constraint.metadata.database.BeanMetadataRepository;
import nl._42.jarb.constraint.metadata.database.ColumnMetadata;
import nl._42.jarb.utils.bean.Beans;
import nl._42.jarb.utils.bean.FlexibleBeanWrapper;
import nl._42.jarb.utils.bean.PropertyReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidatorContext;
import javax.validation.MessageInterpolator;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static nl._42.jarb.utils.StringUtils.isNotBlank;

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

    private void validateProperty(FlexibleBeanWrapper beanWrapper, PropertyReference property, BasePath basePath, DatabaseValidationContext validation) {
        Field field = Beans.findPropertyField(property);

        if (isValidatable(field)) {
            Class<?> propertyType = field.getType();
            if (beanMetadataRepository.isEmbeddable(propertyType)) {
                for (String propertyName : Beans.getFieldNames(propertyType)) {
                    validateProperty(beanWrapper, new PropertyReference(property, propertyName), basePath, validation);
                }
            } else {
                validateSimpleProperty(beanWrapper, property, propertyType, basePath, validation);
            }
        }
    }

    private boolean isValidatable(Field field) {
        return !Modifier.isStatic(field.getModifiers()) && field.getAnnotation(IgnoreDatabaseConstraints.class) == null;
    }

    private void validateSimpleProperty(FlexibleBeanWrapper bean, PropertyReference property, Class<?> propertyType, BasePath basePath, DatabaseValidationContext validation) {
        PropertyReference wrapped = basePath.apply(property);

        ColumnMetadata columnMetadata = beanMetadataRepository.getColumnMetadata(wrapped);
        if (columnMetadata != null) {
            Object propertyValue = bean.getPropertyValue(property.getPropertyName());
            for (DatabaseConstraintValidationStep validationStep : validationSteps) {
                validationStep.validate(propertyValue, propertyType, property, columnMetadata, validation);
            }
        } else {
            logger.debug("Skipped validation because no meta-data could be found for property '{}'.", wrapped);
        }
    }

    public void addValidationStep(DatabaseConstraintValidationStep validationStep) {
        validationSteps.add(validationStep);
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
