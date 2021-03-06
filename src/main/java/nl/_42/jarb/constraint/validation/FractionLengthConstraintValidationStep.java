package nl._42.jarb.constraint.validation;

import java.math.BigDecimal;

import nl._42.jarb.constraint.metadata.database.ColumnMetadata;
import nl._42.jarb.utils.bean.PropertyReference;

public class FractionLengthConstraintValidationStep implements DatabaseConstraintValidationStep {

    private static final String FRACTION_LENGTH_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.FractionLength.message}";

    @Override
    public void validate(Object value, Class<?> valueType, PropertyReference reference, ColumnMetadata metadata, DatabaseValidationContext context) {
        if (isFractionLengthExceeded(value, metadata)) {
            context.buildViolationWithTemplate(reference, FRACTION_LENGTH_TEMPLATE)
                    .attribute("max", metadata.getFractionLength())
                    .value(value)
                    	.addToContext();
        }
    }

    private boolean isFractionLengthExceeded(Object value, ColumnMetadata metadata) {
        boolean exceeded = false;
        if (metadata.hasFractionLength() && value instanceof Number) {
        	int fractionLength = getFractionLength((Number) value);
            exceeded = fractionLength > metadata.getFractionLength();
        }
        return exceeded;
    }

    private int getFractionLength(Number number) {
        BigDecimal numberAsBigDecimal = new BigDecimal(number.toString());
        return numberAsBigDecimal.scale() < 0 ? 0 : numberAsBigDecimal.scale();
    }

}
