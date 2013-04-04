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
import org.jarbframework.utils.bean.ModifiableBean;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.ColumnReference;
import org.jarbframework.utils.orm.SchemaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.NullValueInNestedPathException;

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
    private final Logger logger = LoggerFactory.getLogger(DatabaseConstraintValidator.class);

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
     * @param validatorContext used to create constraint violations
     * @return whether the bean is valid, or not
     */
    public boolean isValid(Object bean, ConstraintValidatorContext validatorContext) {
        DatabaseConstraintValidationContext validation = new DatabaseConstraintValidationContext(validatorContext, messageBuilder);
        validateBean(bean, validation);
        return validation.isValid();
    }

    private void validateBean(Object bean, DatabaseConstraintValidationContext validation) {
        for (String propertyName : BeanProperties.getFieldNames(bean.getClass())) {
            validateProperty(bean, new PropertyReference(bean.getClass(), propertyName), validation);
        }
    }

    private void validateProperty(Object bean, PropertyReference propertyRef, DatabaseConstraintValidationContext validation) {
        Field propertyField = BeanProperties.findPropertyField(propertyRef);
        if (!Modifier.isStatic(propertyField.getModifiers())) {
            Class<?> propertyClass = propertyField.getType();
            if (schemaMapper.isEmbeddable(propertyClass)) {
                validateNestedProperty(bean, propertyRef, validation, propertyClass);
            } else {
                validateDirectProperty(bean, propertyRef, validation);
            }
        }
    }

    private void validateNestedProperty(Object bean, PropertyReference propertyRef, DatabaseConstraintValidationContext validation, Class<?> propertyClass) {
        for (String embbededPropertyName : BeanProperties.getFieldNames(propertyClass)) {
            validateProperty(bean, new PropertyReference(propertyRef, embbededPropertyName), validation);
        }
    }

    private void validateDirectProperty(Object bean, PropertyReference propertyRef, DatabaseConstraintValidationContext validation) {
        ColumnReference columnRef = schemaMapper.getColumnReference(propertyRef);
        if (columnRef != null) {
            ColumnMetadata columnMetadata = columnMetadataRepository.getColumnMetadata(columnRef);
            if (columnMetadata != null) {
                Object propertyValue = null;
                try {
                    propertyValue = ModifiableBean.wrap(bean).getPropertyValue(propertyRef.getName());
                } catch (NullValueInNestedPathException e) {
                    logger.debug("Could not retrieve actual property value.", e);
                }
                for (DatabaseConstraintValidationStep step : validationSteps) {
                    step.validate(propertyValue, propertyRef, columnMetadata, validation);
                }
            } else {
                logger.warn("Skipped validation because no metadata could be found for column '{}'.", columnRef);
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