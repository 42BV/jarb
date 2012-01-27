package org.jarbframework.validation;

import static org.jarbframework.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import org.jarbframework.constraint.database.ColumnMetadata;
import org.jarbframework.constraint.database.DatabaseGenerated;
import org.jarbframework.utils.bean.PropertyReference;

public class NotNullConstraintValidationStep implements DatabaseConstraintValidationStep {

    private static final String NOT_NULL_VIOLATION_TEMPLATE = "{javax.validation.constraints.NotNull.message}";

    @Override
    public void validate(Object propertyValue, PropertyReference propertyRef, ColumnMetadata columnMetadata, DatabaseConstraintValidationContext context) {
        if (propertyValue == null && valueIsExpected(propertyRef, columnMetadata)) {
            context.buildViolationWithTemplate(propertyRef, NOT_NULL_VIOLATION_TEMPLATE).addToContext();
        }
    }

    private boolean valueIsExpected(PropertyReference propertyRef, ColumnMetadata columnMetadata) {
        return columnMetadata.isRequired() && !isGeneratable(propertyRef, columnMetadata);
    }

    private boolean isGeneratable(PropertyReference propertyRef, ColumnMetadata columnMetadata) {
        return columnMetadata.isGeneratable() || fieldOrGetter().hasAnnotation(propertyRef, DatabaseGenerated.class);
    }

}
