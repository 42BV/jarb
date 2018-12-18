package nl._42.jarb.constraint.validation;

import nl._42.jarb.constraint.metadata.DatabaseGenerated;
import nl._42.jarb.constraint.metadata.database.ColumnMetadata;
import nl._42.jarb.utils.bean.Annotations;
import nl._42.jarb.utils.bean.PropertyReference;

public class NotNullConstraintValidationStep implements DatabaseConstraintValidationStep {

    private static final String NOT_NULL_VIOLATION_TEMPLATE = "{javax.validation.constraints.NotNull.message}";

    @Override
    public void validate(Object value, PropertyReference reference, ColumnMetadata metadata, DatabaseValidationContext context) {
        if (value == null && isValueExpected(reference, metadata)) {
            context.buildViolationWithTemplate(reference, NOT_NULL_VIOLATION_TEMPLATE).addToContext();
        }
    }

    private boolean isValueExpected(PropertyReference reference, ColumnMetadata metadata) {
        return metadata.isRequired() && ! isGeneratable(reference, metadata);
    }

    private boolean isGeneratable(PropertyReference reference, ColumnMetadata metadata) {
        return metadata.isGeneratable() || Annotations.hasAnnotation(reference, DatabaseGenerated.class);
    }

}
