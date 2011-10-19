package org.jarbframework.validation;

import static org.jarbframework.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import org.jarbframework.constraint.DatabaseGenerated;
import org.jarbframework.constraint.database.column.ColumnMetadata;
import org.jarbframework.utils.bean.PropertyReference;

public class NotNullConstraintValidationStep implements DatabaseConstraintValidationStep {
    private static final String NOT_NULL_VIOLATION_TEMPLATE = "{javax.validation.constraints.NotNull.message}";

    @Override
    public void validate(Object propertyValue, PropertyReference propertyRef, ColumnMetadata columnMetadata, DatabaseConstraintValidationContext validation) {
        if (propertyValue == null && valueIsExpected(propertyRef, columnMetadata)) {
            validation.buildViolationWithTemplate(propertyRef, NOT_NULL_VIOLATION_TEMPLATE).addViolation();
        }
    }

    private boolean valueIsExpected(PropertyReference propertyRef, ColumnMetadata columnMetadata) {
        return columnMetadata.isRequired() && !isGeneratable(propertyRef, columnMetadata);
    }

    private boolean isGeneratable(PropertyReference propertyRef, ColumnMetadata columnMetadata) {
        return columnMetadata.isGeneratable() || fieldOrGetter().hasAnnotation(propertyRef, DatabaseGenerated.class);
    }

}
