package nl._42.jarb.constraint.validation;

import nl._42.jarb.constraint.metadata.DatabaseGenerated;
import nl._42.jarb.constraint.metadata.database.ColumnMetadata;
import nl._42.jarb.utils.bean.Annotations;
import nl._42.jarb.utils.bean.PropertyReference;

import java.util.Collection;
import java.util.Map;

public class NotNullConstraintValidationStep implements DatabaseConstraintValidationStep {

    private static final String NOT_NULL_VIOLATION_TEMPLATE = "{javax.validation.constraints.NotNull.message}";

    @Override
    public void validate(Object value, Class<?> valueType, PropertyReference reference, ColumnMetadata metadata, DatabaseValidationContext context) {
        if (value == null && isSupported(valueType) && isExpected(reference, metadata)) {
            context.buildViolationWithTemplate(reference, NOT_NULL_VIOLATION_TEMPLATE).addToContext();
        }
    }

    private boolean isSupported(Class<?> valueType) {
        return !(Collection.class.isAssignableFrom(valueType) || Map.class.isAssignableFrom(valueType));
    }

    private boolean isExpected(PropertyReference reference, ColumnMetadata metadata) {
        boolean expected = false;
        if (metadata.isRequired()) {
            expected = !isGenerated(reference, metadata);
        }
        return expected;
    }

    private boolean isGenerated(PropertyReference reference, ColumnMetadata metadata) {
        return metadata.isGeneratable() || Annotations.hasAnnotation(reference, DatabaseGenerated.class);
    }

}
