package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.violaton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.MessageViolationResolver;

/**
 * Oracle based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class OracleViolationResolver implements MessageViolationResolver {

    /* Provided: schema and check name */
    private static final Pattern CHECK_FAILED_PATTERN = Pattern.compile("(.+): check constraint \\((.+)\\.(.+)\\) violated\n");

    /* Provided: schema, table and column name */
    private static final Pattern CANNOT_BE_NULL_PATTERN = Pattern.compile("(.+): cannot insert NULL into \\(\"(.+)\"\\.\"(.+)\"\\.\"(.+)\"\\)\n");

    /* Provided: schema and constraint name */
    private static final Pattern UNIQUE_VIOLATION_PATTERN = Pattern.compile("(.+): unique constraint \\((.+)\\.(.+)\\) violated\n");

    /* Provided: schema and constraint name */
    private static final Pattern FK_VIOLATION_PATTERN = Pattern.compile("(.+): integrity constraint \\((.+)\\.(.+)\\) violated - child record found\n");

    /* Provided: schema, table and column name, actual length, maximum length */
    private static final Pattern LENGTH_EXCEEDED_PATTERN = Pattern.compile("(.+): value too large for column \"(.+)\"\\.\"(.+)\"\\.\"(.+)\" \\(actual: (\\d+), maximum: (\\d+)\\)\n");

    /* Provided: column type */
    private static final Pattern INVALID_TYPE_PATTERN = Pattern.compile("(.+): invalid (.+)\n");

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolation resolve(String message) {
        DatabaseConstraintViolation violation = null;
        
        Matcher matcher;
        if ((matcher = CHECK_FAILED_PATTERN.matcher(message)).matches()) {
            violation = resolveCheckViolation(matcher);
        } else if ((matcher = CANNOT_BE_NULL_PATTERN.matcher(message)).matches()) {
            violation = resolveNotNullViolation(matcher);
        } else if ((matcher = UNIQUE_VIOLATION_PATTERN.matcher(message)).matches()) {
            violation = resolveUniqueKeyViolation(matcher);
        } else if ((matcher = FK_VIOLATION_PATTERN.matcher(message)).matches()) {
            violation = resolveForeignKeyViolation(matcher);
        } else if ((matcher = LENGTH_EXCEEDED_PATTERN.matcher(message)).matches()) {
            violation = resolveLengthViolation(matcher);
        } else if ((matcher = INVALID_TYPE_PATTERN.matcher(message)).matches()) {
            violation = resolveTypeViolation(matcher);
        }
        
        return violation;
    }

    private DatabaseConstraintViolation resolveCheckViolation(Matcher matcher) {
        return violaton(CHECK_FAILED)
                .constraint(matcher.group(3))
                    .build();
    }

    private DatabaseConstraintViolation resolveUniqueKeyViolation(Matcher matcher) {
        return violaton(UNIQUE_KEY)
                .constraint(matcher.group(3))
                    .build();
    }

    private DatabaseConstraintViolation resolveForeignKeyViolation(Matcher matcher) {
        return violaton(FOREIGN_KEY)
                .constraint(matcher.group(3))
                    .build();
    }

    private DatabaseConstraintViolation resolveNotNullViolation(Matcher matcher) {
        return violaton(NOT_NULL)
                .table(matcher.group(3))
                .column(matcher.group(4))
                    .build();
    }

    private DatabaseConstraintViolation resolveLengthViolation(Matcher matcher) {
        return violaton(LENGTH_EXCEEDED)
                .table(matcher.group(3))
                .column(matcher.group(4))
                .maximumLength(matcher.group(6))
                    .build();
    }

    private DatabaseConstraintViolation resolveTypeViolation(Matcher matcher) {
        return violaton(INVALID_TYPE)
                .expectedValueType(matcher.group(2))
                    .build();
    }
}
