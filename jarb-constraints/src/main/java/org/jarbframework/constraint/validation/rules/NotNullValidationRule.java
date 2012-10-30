package org.jarbframework.constraint.validation.rules;

import static org.jarbframework.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.constraint.metadata.database.DatabaseGenerated;
import org.jarbframework.constraint.validation.DatabaseConstraintValidationContext;
import org.jarbframework.utils.bean.PropertyReference;

class NotNullValidationRule implements PropertyValueValidationRule {

    private static final String NOT_NULL_VIOLATION_TEMPLATE = "{javax.validation.constraints.NotNull.message}";

    @Override
    public void validate(Object propertyValue, PropertyReference propertyReference, ColumnMetadata columnMetadata, DatabaseConstraintValidationContext context) {
        if (propertyValue == null && valueIsExpected(propertyReference, columnMetadata)) {
            context.buildViolationWithTemplate(propertyReference, NOT_NULL_VIOLATION_TEMPLATE).addToContext();
        }
    }

    private boolean valueIsExpected(PropertyReference propertyReference, ColumnMetadata columnMetadata) {
        return columnMetadata.isRequired() && !isGeneratable(propertyReference, columnMetadata);
    }

    private boolean isGeneratable(PropertyReference propertyReference, ColumnMetadata columnMetadata) {
        return columnMetadata.isGeneratable() || fieldOrGetter().hasAnnotation(propertyReference, DatabaseGenerated.class);
    }

}
