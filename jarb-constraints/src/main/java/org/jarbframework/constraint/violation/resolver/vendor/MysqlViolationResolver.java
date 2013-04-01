package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.violaton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.ViolationMessageResolver;

/**
 * MySQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class MysqlViolationResolver implements ViolationMessageResolver {

    /* Provided: column name */
    private static final Pattern CANNOT_BE_NULL_PATTERN = Pattern.compile("Column '(.+)' cannot be null");

    /* Provided: value, constraint name */
    private static final Pattern UNIQUE_VIOLATION_PATTERN = Pattern.compile("Duplicate entry '(.+)' for key '(.+)'");

    /* Provided: column name */
    private static final Pattern LENGTH_EXCEEDED_PATTERN = Pattern.compile("Data truncation: Data too long for column '(.+)' at row (\\d+)");

    /* Provided: column type, value, column name */
    private static final Pattern INVALID_TYPE_PATTERN = Pattern.compile("Incorrect (\\w+) value: '(.+)' for column '(.+)' at row (\\d+)");

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolation resolveByMessage(String message) {
        DatabaseConstraintViolation violation = null;
        
        Matcher matcher;
        if ((matcher = CANNOT_BE_NULL_PATTERN.matcher(message)).matches()) {
            violation = resolveNotNullViolation(matcher);
        } else if ((matcher = UNIQUE_VIOLATION_PATTERN.matcher(message)).matches()) {
            violation = resolveUniqueKeyViolation(matcher);
        } else if ((matcher = LENGTH_EXCEEDED_PATTERN.matcher(message)).matches()) {
            violation = resolveLengthViolation(matcher);
        } else if ((matcher = INVALID_TYPE_PATTERN.matcher(message)).matches()) {
            violation = resolveTypeViolation(matcher);
        }
        
        return violation;
    }

    private DatabaseConstraintViolation resolveNotNullViolation(Matcher matcher) {
        return violaton(NOT_NULL)
                .column(matcher.group(1))
                    .build();
    }

    private DatabaseConstraintViolation resolveUniqueKeyViolation(Matcher matcher) {
        return violaton(UNIQUE_KEY)
                .value(matcher.group(1))
                .constraint(matcher.group(2))
                    .build();
    }

    private DatabaseConstraintViolation resolveLengthViolation(Matcher matcher) {
        return violaton(LENGTH_EXCEEDED)
                .column(matcher.group(1))
                    .build();
    }

    private DatabaseConstraintViolation resolveTypeViolation(Matcher matcher) {
        return violaton(INVALID_TYPE)
                .expectedValueType(matcher.group(1))
                .value(matcher.group(2))
                .column(matcher.group(3))
                    .build();
    }
}
