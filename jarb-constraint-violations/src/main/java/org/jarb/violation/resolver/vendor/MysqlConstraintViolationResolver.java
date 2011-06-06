package org.jarb.violation.resolver.vendor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationType;
import org.jarb.violation.resolver.RootCauseMessageConstraintViolationResolver;
import org.springframework.util.Assert;

/**
 * MySQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class MysqlConstraintViolationResolver extends RootCauseMessageConstraintViolationResolver {

    private static final String CANNOT_BE_NULL_PATTERN
    /* Provided: column name */
    = "Column '(.+)' cannot be null";

    private static final String UNIQUE_VIOLATION_PATTERN
    /* Provided: value, constraint name */
    = "Duplicate entry '(.+)' for key '(.+)'";

    private static final String LENGTH_EXCEEDED_PATTERN
    /* Provided: column name */
    = "Data truncation: Data too long for column '(.+)' at row (\\d+)";

    private static final String INVALID_TYPE_PATTERN
    /* Provided: column type, value, column name */
    = "Incorrect (\\w+) value: '(.+)' for column '(.+)' at row (\\d+)";

    /**
     * {@inheritDoc}
     */
    @Override
    protected ConstraintViolation resolveByMessage(String message) {
        ConstraintViolation violation = null;
        if (message.matches(CANNOT_BE_NULL_PATTERN)) {
            violation = resolveNotNullViolation(message);
        } else if (message.matches(UNIQUE_VIOLATION_PATTERN)) {
            violation = resolveUniqueKeyViolation(message);
        } else if (message.matches(LENGTH_EXCEEDED_PATTERN)) {
            violation = resolveLengthViolation(message);
        } else if (message.matches(INVALID_TYPE_PATTERN)) {
            violation = resolveTypeViolation(message);
        }
        return violation;
    }

    private ConstraintViolation resolveNotNullViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.CANNOT_BE_NULL);
        Matcher matcher = Pattern.compile(CANNOT_BE_NULL_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setColumnName(matcher.group(1));
        return violationBuilder.build();
    }

    private ConstraintViolation resolveUniqueKeyViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.UNIQUE_VIOLATION);
        Matcher matcher = Pattern.compile(UNIQUE_VIOLATION_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setValue(matcher.group(1));
        violationBuilder.setConstraintName(matcher.group(2));
        return violationBuilder.build();
    }

    private ConstraintViolation resolveLengthViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.LENGTH_EXCEEDED);
        Matcher matcher = Pattern.compile(LENGTH_EXCEEDED_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setColumnName(matcher.group(1));
        return violationBuilder.build();
    }

    private ConstraintViolation resolveTypeViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.INVALID_TYPE);
        Matcher matcher = Pattern.compile(INVALID_TYPE_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setExpectedType(matcher.group(1));
        violationBuilder.setValue(matcher.group(2));
        violationBuilder.setColumnName(matcher.group(3));
        return violationBuilder.build();
    }
}
