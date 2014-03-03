package org.jarbframework.constraint.validation;

import static org.jarbframework.utils.bean.AnnotationScanner.fieldOrGetter;

import org.jarbframework.constraint.metadata.DatabaseGenerated;
import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.utils.bean.PropertyReference;

public class NotNullConstraintValidationStep implements DatabaseConstraintValidationStep {

    private static final String NOT_NULL_VIOLATION_TEMPLATE = "{javax.validation.constraints.NotNull.message}";

    @Override
    public void validate(Object value, PropertyReference reference, ColumnMetadata metadata, DatabaseConstraintValidationContext context) {
        if (value == null && isValueExpected(reference, metadata)) {
            context.buildViolationWithTemplate(reference, NOT_NULL_VIOLATION_TEMPLATE).addToContext();
        }
    }

    private boolean isValueExpected(PropertyReference reference, ColumnMetadata metadata) {
        return metadata.isRequired() && ! isGeneratable(reference, metadata);
    }

    private boolean isGeneratable(PropertyReference reference, ColumnMetadata metadata) {
        return metadata.isGeneratable() || fieldOrGetter().hasAnnotation(reference, DatabaseGenerated.class);
    }

}
