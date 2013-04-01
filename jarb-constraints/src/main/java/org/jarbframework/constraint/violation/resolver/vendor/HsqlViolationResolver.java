package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
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
 * Hypersonic SQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class HsqlViolationResolver implements ViolationMessageResolver {

    /* Provided: constraint name, table name, column name */
    private static final Pattern CANNOT_BE_NULL_PATTERN = Pattern.compile("integrity constraint violation: NOT NULL check constraint; (.+) table: (.+) column: (.+)");

    /* Provided: constraint name, table name */
    private static final Pattern UNIQUE_VIOLATION_PATTERN = Pattern.compile("integrity constraint violation: unique constraint or index violation; (.+) table: (.+)");
    
    /* Provided: constraint name, table name */
    private static final Pattern FK_VIOLATION_PATTERN = Pattern.compile("integrity constraint violation: foreign key no action; (.+) table: (.+)");

    /* Provided: value type */
    private static final Pattern LENGTH_EXCEEDED_PATTERN = Pattern.compile("data exception: (.+) data, right truncation");

    /* Provided: value type */
    private static final Pattern INVALID_TYPE_PATTERN = Pattern.compile("data exception: invalid (.+) value for cast");

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
            violation = resolveUniqueViolation(matcher);
        } else if ((matcher = FK_VIOLATION_PATTERN.matcher(message)).matches()) {
            violation = resolveForeignKeyViolation(matcher);
        } else if ((matcher = LENGTH_EXCEEDED_PATTERN.matcher(message)).matches()) {
            violation = resolveLengthViolation(matcher);
        } else if ((matcher = INVALID_TYPE_PATTERN.matcher(message)).matches()) {
            violation = resolveTypeViolation(matcher);
        }
        
        return violation;
    }

    private DatabaseConstraintViolation resolveForeignKeyViolation(Matcher matcher) {
        return violaton(FOREIGN_KEY)
                .constraint(matcher.group(1).toLowerCase())
                .table(matcher.group(2).toLowerCase())
                    .build();
    }

    private DatabaseConstraintViolation resolveNotNullViolation(Matcher matcher) {
        return violaton(NOT_NULL)
                .constraint(matcher.group(1).toLowerCase())
                .table(matcher.group(2).toLowerCase())
                .column(matcher.group(3).toLowerCase())
                    .build();
    }

    private DatabaseConstraintViolation resolveUniqueViolation(Matcher matcher) {
        return violaton(UNIQUE_KEY)
                .constraint(matcher.group(1).toLowerCase())
                .table(matcher.group(2).toLowerCase())
                    .build();
    }

    private DatabaseConstraintViolation resolveLengthViolation(Matcher matcher) {
        return violaton(LENGTH_EXCEEDED)
                .valueType(matcher.group(1).toLowerCase())
                    .build();
    }

    private DatabaseConstraintViolation resolveTypeViolation(Matcher matcher) {
        return violaton(INVALID_TYPE)
                .valueType(matcher.group(1).toLowerCase())
                    .build();
    }

}
