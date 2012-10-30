package org.jarbframework.constraint.validation.rules;

import java.math.BigDecimal;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.constraint.validation.DatabaseConstraintValidationContext;
import org.jarbframework.utils.bean.PropertyReference;

class FractionLengthValidationRule implements PropertyValueValidationRule {

    private static final String FRACTION_LENGTH_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.FractionLength.message}";

    @Override
    public void validate(Object propertyValue, PropertyReference propertyReference, ColumnMetadata columnMetadata, DatabaseConstraintValidationContext context) {
        if (fractionLengthExceeded(propertyValue, columnMetadata)) {
            context.buildViolationWithTemplate(propertyReference, FRACTION_LENGTH_TEMPLATE)
                        .attribute("max", columnMetadata.getFractionLength())
                        .value(propertyValue)
                            .addToContext();
        }
    }

    private boolean fractionLengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
        boolean lengthExceeded = false;
        if ((columnMetadata.hasFractionLength()) && (propertyValue instanceof Number)) {
            lengthExceeded = lengthOfFraction((Number) propertyValue) > columnMetadata.getFractionLength();
        }
        return lengthExceeded;
    }

    private int lengthOfFraction(Number number) {
        BigDecimal numberAsBigDecimal = new BigDecimal(number.toString());
        return numberAsBigDecimal.scale() < 0 ? 0 : numberAsBigDecimal.scale();
    }

}
