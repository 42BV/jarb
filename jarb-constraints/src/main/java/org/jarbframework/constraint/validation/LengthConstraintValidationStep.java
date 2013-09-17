package org.jarbframework.constraint.validation;

import java.math.BigDecimal;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.utils.bean.PropertyReference;

public class LengthConstraintValidationStep implements DatabaseConstraintValidationStep {

    private static final String LENGTH_VIOLATION_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.Length.message}";

    @Override
    public void validate(Object propertyValue, PropertyReference propertyRef, ColumnMetadata columnMetadata, DatabaseConstraintValidationContext context) {
        if (isLengthExceeded(propertyValue, columnMetadata)) {
            context.buildViolationWithTemplate(propertyRef, LENGTH_VIOLATION_TEMPLATE)
                    .attribute("max", columnMetadata.getMaximumLength())
                    .value(propertyValue)
                    	.addToContext();
        }
    }

    private boolean isLengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
        boolean lengthExceeded = false;
        if (columnMetadata.hasMaximumLength()) {
        	int length = getLength(propertyValue);
			lengthExceeded = length > columnMetadata.getMaximumLength();
        }
        return lengthExceeded;
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
