package nl._42.jarb.constraint.validation;

import java.math.BigDecimal;

import nl._42.jarb.constraint.metadata.database.ColumnMetadata;
import nl._42.jarb.utils.bean.PropertyReference;

public class LengthConstraintValidationStep implements DatabaseConstraintValidationStep {

    private static final String LENGTH_VIOLATION_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.Length.message}";

    @Override
    public void validate(Object value, PropertyReference reference, ColumnMetadata metadata, DatabaseValidationContext context) {
        if (isLengthExceeded(value, metadata)) {
            context.buildViolationWithTemplate(reference, LENGTH_VIOLATION_TEMPLATE)
                    .attribute("max", metadata.getMaximumLength())
                    .value(value)
                    	.addToContext();
        }
    }

    private boolean isLengthExceeded(Object value, ColumnMetadata metadata) {
        boolean exceeded = false;
        if (metadata.hasMaximumLength()) {
        	int length = getLength(value);
			exceeded = length > metadata.getMaximumLength();
        }
        return exceeded;
    }
    
    private int getLength(Object value) {
    	int length = -1;
    	if (value instanceof String) {
            length = ((String) value).length();
        } else if (value instanceof Number) {
            length = new BigDecimal(value.toString()).precision();
        }
    	return length;
    }

}
