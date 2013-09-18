package org.jarbframework.constraint.validation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.MessageInterpolator;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.constraint.metadata.database.ColumnMetadataRepository;
import org.jarbframework.utils.bean.BeanProperties;
import org.jarbframework.utils.bean.DynamicBeanWrapper;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.ColumnReference;
import org.jarbframework.utils.orm.SchemaMapper;
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
    private ColumnMetadataRepository columnMetadataRepository;
    
    /** Used to build violation messages **/
    private ViolationMessageBuilder messageBuilder;
    
    /** Maps bean properties to database columns, for which we query meta-data **/
    private SchemaMapper schemaMapper;

    /**
     * Construct a new {@link DatabaseConstraintValidator}.
     */
    public DatabaseConstraintValidator() {
        validationSteps = new ArrayList<DatabaseConstraintValidationStep>();
        validationSteps.add(new NotNullConstraintValidationStep());
        validationSteps.add(new LengthConstraintValidationStep());
        validationSteps.add(new FractionLengthConstraintValidationStep());
    }

    /**
     * Determine if a bean follows all column constraints defined in the database.
     * @param bean the bean that should be validated
     * @param beanReference the reference to our bean, or {@code null} if the bean is our root
     * @param validatorContext used to create constraint violations
     * @return whether the bean is valid, or not
     */
	public boolean isValid(Object bean, PropertyReference beanReference, ConstraintValidatorContext validatorContext) {
        DatabaseConstraintValidationContext validation = new DatabaseConstraintValidationContext(validatorContext, messageBuilder);
        validateBean(bean, beanReference, validation);
        return validation.isValid();
	}

    private void validateBean(Object bean, PropertyReference beanReference, DatabaseConstraintValidationContext validation) {
    	DynamicBeanWrapper<?> beanWrapper = DynamicBeanWrapper.wrap(bean);
        for (String propertyName : BeanProperties.getFieldNames(bean.getClass())) {
			validateProperty(beanWrapper, new PropertyReference(bean.getClass(), propertyName), beanReference, validation);
        }
    }

    private void validateProperty(DynamicBeanWrapper<?> beanWrapper, PropertyReference propertyReference, PropertyReference beanReference, DatabaseConstraintValidationContext validation) {
        Field propertyField = BeanProperties.findPropertyField(propertyReference);
        if (! Modifier.isStatic(propertyField.getModifiers())) {
            Class<?> propertyClass = propertyField.getType();
            if (schemaMapper.isEmbeddable(propertyClass)) {
            	for (String propertyName : BeanProperties.getFieldNames(propertyClass)) {
                    validateProperty(beanWrapper, new PropertyReference(propertyReference, propertyName), beanReference, validation);
                }
            } else {
                validateSimpleProperty(beanWrapper, propertyReference, beanReference, validation);
            }
        }
    }

    private void validateSimpleProperty(DynamicBeanWrapper<?> beanWrapper, PropertyReference propertyReference, PropertyReference beanReference, DatabaseConstraintValidationContext validation) {
        ColumnReference columnReference = schemaMapper.getColumnReference(propertyReference.wrap(beanReference));
        if (columnReference != null) {
            ColumnMetadata columnMetadata = columnMetadataRepository.getColumnMetadata(columnReference);
            if (columnMetadata != null) {
                Object propertyValue = beanWrapper.getPropertyValueSafely(propertyReference.getName());
                for (DatabaseConstraintValidationStep validationStep : validationSteps) {
                    validationStep.validate(propertyValue, propertyReference, columnMetadata, validation);
                }
            } else {
                logger.warn("Skipped validation because no metadata could be found for column '{}'.", columnReference);
            }
        }
    }

    public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
        messageBuilder = new ViolationMessageBuilder(messageInterpolator);
    }

    public void setSchemaMapper(SchemaMapper schemaMapper) {
        this.schemaMapper = schemaMapper;
    }
    
    public void setColumnMetadataRepository(ColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }
    
}
