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
    private final List<DatabaseConstraintValidationStep> validationSteps;
    
    private DatabaseConstraintRepository constraintRepository;
    private ViolationMessageBuilder messageBuilder;
    private SchemaMapper schemaMapper;

    public DatabaseConstraintValidator() {
        validationSteps = new ArrayList<DatabaseConstraintValidationStep>();
        validationSteps.add(new NotNullConstraintValidationStep());
        validationSteps.add(new LengthConstraintValidationStep());
        validationSteps.add(new FractionLengthConstraintValidationStep());
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
            for (String embbededPropertyName : BeanProperties.getFieldNames(propertyClass)) {
                validateProperty(bean, new PropertyReference(propertyRef, embbededPropertyName), validation);
            }
        } else {
            ColumnReference columnRef = schemaMapper.columnOf(propertyRef);
            if (columnRef != null) {
                validateColumnConstraints(bean, propertyRef, columnRef, validation);
            }
        }
    }

    private void validateColumnConstraints(Object bean, PropertyReference propertyRef, ColumnReference columnRef, DatabaseConstraintValidationContext validation) {
        ColumnMetadata columnMetadata = constraintRepository.getColumnMetadata(columnRef);
        if(columnMetadata != null) {
            Object propertyValue = ModifiableBean.wrap(bean).getPropertyValue(propertyRef.getName());
            for (DatabaseConstraintValidationStep validationStep : validationSteps) {
                validationStep.validate(propertyValue, propertyRef, columnMetadata, validation);
            }
        } else {
            logger.warn("Skipped validation because no metadata could be found for column '{}'.", columnRef);
        }
    }

}
