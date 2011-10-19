package org.jarbframework.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.MessageInterpolator;

import org.jarbframework.constraint.database.DatabaseConstraintRepository;
import org.jarbframework.constraint.database.column.ColumnMetadata;
import org.jarbframework.utils.bean.BeanProperties;
import org.jarbframework.utils.bean.ModifiableBean;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.ColumnReference;
import org.jarbframework.utils.orm.SchemaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConstraintValidator {
    private final Logger logger = LoggerFactory.getLogger(DatabaseConstraintValidator.class);
    private final List<DatabaseConstraintValidationStep> steps;
    
    private DatabaseConstraintRepository constraintRepository;
    private ViolationMessageBuilder messageBuilder;
    private SchemaMapper schemaMapper;

    public DatabaseConstraintValidator() {
        steps = new ArrayList<DatabaseConstraintValidationStep>();
        steps.add(new NotNullConstraintValidationStep());
        steps.add(new LengthConstraintValidationStep());
        steps.add(new FractionLengthConstraintValidationStep());
    }
    
    public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
        messageBuilder = new ViolationMessageBuilder(messageInterpolator);
    }
    
    public void setConstraintRepository(DatabaseConstraintRepository constraintRepository) {
        this.constraintRepository = constraintRepository;
    }
    
    public void setSchemaMapper(SchemaMapper schemaMapper) {
        this.schemaMapper = schemaMapper;
    }

    public boolean isValid(Object bean, ConstraintValidatorContext validatorContext) {
        DatabaseConstraintValidationContext validation = new DatabaseConstraintValidationContext(validatorContext, messageBuilder);
        for (String propertyName : BeanProperties.getFieldNames(bean.getClass())) {
            validateProperty(bean, new PropertyReference(bean.getClass(), propertyName), validation);
        }
        return validation.isValid();
    }

    private void validateProperty(Object bean, PropertyReference propertyRef, DatabaseConstraintValidationContext validation) {
        Class<?> propertyClass = BeanProperties.getPropertyType(propertyRef);
        if (schemaMapper.isEmbeddable(propertyClass)) {
            validateNestedProperty(bean, propertyRef, validation, propertyClass);
        } else {
            validateSimpleProperty(bean, propertyRef, validation);
        }
    }

    private void validateNestedProperty(Object bean, PropertyReference propertyRef, DatabaseConstraintValidationContext validation, Class<?> propertyClass) {
        for (String embbededPropertyName : BeanProperties.getFieldNames(propertyClass)) {
            validateProperty(bean, new PropertyReference(propertyRef, embbededPropertyName), validation);
        }
    }

    private void validateSimpleProperty(Object bean, PropertyReference propertyRef, DatabaseConstraintValidationContext validation) {
        ColumnReference columnRef = schemaMapper.columnOf(propertyRef);
        if (columnRef != null) {
            ColumnMetadata columnMetadata = constraintRepository.getColumnMetadata(columnRef);
            if(columnMetadata != null) {
                Object propertyValue = ModifiableBean.wrap(bean).getPropertyValue(propertyRef.getName());
                for (DatabaseConstraintValidationStep step : steps) {
                    step.validate(propertyValue, propertyRef, columnMetadata, validation);
                }
            } else {
                logger.warn("Skipped validation because no metadata could be found for column '{}'.", columnRef);
            }
        }
    }
}
