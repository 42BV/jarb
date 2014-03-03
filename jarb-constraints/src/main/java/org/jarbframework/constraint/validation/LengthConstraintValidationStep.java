package org.jarbframework.constraint.validation;

import java.math.BigDecimal;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.utils.bean.PropertyReference;

public class LengthConstraintValidationStep implements DatabaseConstraintValidationStep {

    private static final String LENGTH_VIOLATION_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.Length.message}";

    @Override
    public void validate(Object value, PropertyReference reference, ColumnMetadata metadata, DatabaseConstraintValidationContext context) {
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
