package org.jarbframework.constraint.validation;

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
     * @param root the reference to our bean, or {@code null} if the bean is our root
     * @param validatorContext used to create constraint violations
     * @return whether the bean is valid, or not
     */
	public boolean isValid(Object bean, PropertyReference root, ConstraintValidatorContext validatorContext) {
        DatabaseConstraintValidationContext validation = new DatabaseConstraintValidationContext(validatorContext, messageBuilder);
        validateBean(bean, root, validation);
        return validation.isValid();
	}

    private void validateBean(Object bean, PropertyReference root, DatabaseConstraintValidationContext validation) {
    	FlexibleBeanWrapper<?> beanWrapper = FlexibleBeanWrapper.wrap(bean);
        for (String propertyName : BeanProperties.getFieldNames(bean.getClass())) {
			validateProperty(beanWrapper, new PropertyReference(bean.getClass(), propertyName), root, validation);
        }
    }

    private void validateProperty(FlexibleBeanWrapper<?> beanWrapper, PropertyReference property, PropertyReference root, DatabaseConstraintValidationContext validation) {
        Field propertyField = BeanProperties.findPropertyField(property);
        if (! Modifier.isStatic(propertyField.getModifiers())) {
            Class<?> propertyClass = propertyField.getType();
            if (beanMetadataRepository.isEmbeddable(propertyClass)) {
            	for (String propertyName : BeanProperties.getFieldNames(propertyClass)) {
                    validateProperty(beanWrapper, new PropertyReference(property, propertyName), root, validation);
                }
            } else {
                validateSimpleProperty(beanWrapper, property, root, validation);
            }
        }
    }

    private void validateSimpleProperty(FlexibleBeanWrapper<?> beanWrapper, PropertyReference property, PropertyReference root, DatabaseConstraintValidationContext validation) {
        PropertyReference entityPropertyReference = property;
        if (root != null) {
            entityPropertyReference = new PropertyReference(root, property.getName());
        }
        
        ColumnMetadata columnMetadata = beanMetadataRepository.getColumnMetadata(entityPropertyReference);
        if (columnMetadata != null) {
            Object propertyValue = beanWrapper.getPropertyValueNullSafe(property.getName());
            for (DatabaseConstraintValidationStep validationStep : validationSteps) {
                validationStep.validate(propertyValue, property, columnMetadata, validation);
            }
        } else {
            logger.debug("Skipped validation because no meta-data could be found for property '{}'.", entityPropertyReference);
        }
    }

}
