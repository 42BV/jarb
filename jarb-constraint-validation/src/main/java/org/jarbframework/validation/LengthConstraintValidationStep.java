package org.jarbframework.validation;

import java.math.BigDecimal;

import org.jarbframework.constraint.database.column.ColumnMetadata;
import org.jarbframework.utils.bean.PropertyReference;

public class LengthConstraintValidationStep implements DatabaseConstraintValidationStep {
    private static final String LENGTH_VIOLATION_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.Length.message}";

    @Override
    public void validate(Object propertyValue, PropertyReference propertyRef, ColumnMetadata columnMetadata, DatabaseConstraintValidation validation) {
        if(lengthExceeded(propertyValue, columnMetadata)) {
            validation.buildViolationWithTemplate(propertyRef, LENGTH_VIOLATION_TEMPLATE)
                          .attribute("max", columnMetadata.getMaximumLength())
                          .value(propertyValue)
                              .addViolation();
        }
    }
    
    private boolean lengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
        boolean lengthExceeded = false;
        if (columnMetadata.hasMaximumLength()) {
            if (propertyValue instanceof String) {
                lengthExceeded = ((String) propertyValue).length() > columnMetadata.getMaximumLength();
            } else if (propertyValue instanceof Number) {
                lengthExceeded = numberOfDigits((Number) propertyValue) > columnMetadata.getMaximumLength();
            }
        }
        return lengthExceeded;
    }

    private int numberOfDigits(Number number) {
        return new BigDecimal(number.toString()).precision();
    }

}
