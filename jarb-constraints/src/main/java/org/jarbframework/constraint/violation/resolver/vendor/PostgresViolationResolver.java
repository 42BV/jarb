package org.jarbframework.constraint.violation.resolver.vendor;

import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;
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
 * PostgreSQL based constraint violation resolver.
 *
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class PostgresViolationResolver implements MessageViolationResolver {

    /* Provided: column name */
    private static final Pattern CANNOT_BE_NULL_PATTERN = Pattern.compile("ERROR: null value in column \"(.+)\" violates not-null constraint");
    
    /* Provided: column definition (including length) */
    private static final Pattern LENGTH_EXCEEDED_PATTERN = Pattern.compile("ERROR: value too long for type (.+)");
    
    /* Provided: source table name, constraint name, column name, value, target table name */
    private static final Pattern FOREIGN_KEY_VIOLATION_1_PATTERN = Pattern.compile(
            "ERROR: insert or update on table \"(.+)\" violates foreign key constraint \"(.+)\"\\s+"
            + "Detail: Key \\((.+)\\)=\\((.+)\\) is not present in table \"(.+)\"\\.");
    
    /* Provided: target table name, constraint name, source table name, column name, value */
    private static final Pattern FOREIGN_KEY_VIOLATION_2_PATTERN = Pattern.compile(
            "ERROR: update or delete on table \"(.+)\" violates foreign key constraint \"(.+)\" on table \"(.+)\"\\s+"
            + "Detail: Key \\((.+)\\)=\\((.+)\\) is still referenced from table \".+\"\\.");
    
    /* Provided: constraint name, column name, value */
    private static final Pattern UNIQUE_VIOLATION_PATTERN = Pattern.compile(
            "ERROR: duplicate key value violates unique constraint \"(.+)\"\\s+Detail: Key \\((.+)\\)=\\((.+)\\) already exists\\.");
    
    /* Provided: table name, constraint name */
    private static final Pattern CHECK_FAILED_PATTERN = Pattern.compile("ERROR: new row for relation \"(.+)\" violates check constraint \"(.+)\"");
    
    /* Provided: column name, column type, value type */
    private static final Pattern INVALID_TYPE_PATTERN = Pattern.compile("ERROR: column \"(.+)\" is of type (.+) but expression is of type (.+)\\nHint: .*");

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolation resolve(String message) {
        DatabaseConstraintViolation violation = null;

        Matcher matcher;
        if ((matcher = CANNOT_BE_NULL_PATTERN.matcher(message)).matches()) {
            violation = resolveNotNullViolation(matcher);
        } else if ((matcher = LENGTH_EXCEEDED_PATTERN.matcher(message)).matches()) {
            violation = resolveLengthViolation(matcher);
        } else if ((matcher = FOREIGN_KEY_VIOLATION_1_PATTERN.matcher(message)).matches()) {
            violation = resolveForeignKeyViolationOnInsertOrUpdate(matcher);
        } else if ((matcher = FOREIGN_KEY_VIOLATION_2_PATTERN.matcher(message)).matches()) {
            violation = resolveForeignKeyViolationOnUpdateOrDelete(matcher);
        } else if ((matcher = UNIQUE_VIOLATION_PATTERN.matcher(message)).matches()) {
            violation = resolveUniqueKeyViolation(matcher);
        } else if ((matcher = CHECK_FAILED_PATTERN.matcher(message)).matches()) {
            violation = resolveCheckViolation(matcher);
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

    private DatabaseConstraintViolation resolveLengthViolation(Matcher matcher) {
        // Extract type and length from the definition, e.g. varchar(255)
        final String columnDefinition = matcher.group(1);
        String columnType = substringBefore(columnDefinition, "(");
        String columnLength = substringBetween(columnDefinition, "(", ")");
        
        return violaton(LENGTH_EXCEEDED)
                .expectedValueType(columnType)
                .maximumLength(columnLength)
                    .build();
    }

    private DatabaseConstraintViolation resolveForeignKeyViolationOnInsertOrUpdate(Matcher matcher) {
        return violaton(FOREIGN_KEY)
                .constraint(matcher.group(2))
                .table(matcher.group(1))
                .column(matcher.group(3))
                .referencingTable(matcher.group(5))
                .value(matcher.group(4))
                    .build();
    }

    private DatabaseConstraintViolation resolveForeignKeyViolationOnUpdateOrDelete(Matcher matcher) {
        return violaton(FOREIGN_KEY)
                .constraint(matcher.group(2))
                .table(matcher.group(1))
                .column(matcher.group(4))
                .value(matcher.group(5))
                .referencingTable(matcher.group(3))
                    .build();
    }

    private DatabaseConstraintViolation resolveUniqueKeyViolation(Matcher matcher) {
        return violaton(UNIQUE_KEY)
                .constraint(matcher.group(1))
                .column(matcher.group(2))
                .value(matcher.group(3))
                    .build();
    }

    private DatabaseConstraintViolation resolveCheckViolation(Matcher matcher) {
        return violaton(CHECK_FAILED)
                .constraint(matcher.group(2))
                .table(matcher.group(1))
                    .build();
    }

    private DatabaseConstraintViolation resolveTypeViolation(Matcher matcher) {
        return violaton(INVALID_TYPE)
                .column(matcher.group(1))
                .expectedValueType(matcher.group(2))
                .valueType(matcher.group(3))
                    .build();
    }
    
}
