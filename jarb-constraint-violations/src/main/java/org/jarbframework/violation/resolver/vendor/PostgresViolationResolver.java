package org.jarbframework.violation.resolver.vendor;

import static org.jarbframework.violation.DatabaseConstraintViolation.violation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.DatabaseConstraintViolationType;
import org.jarbframework.violation.resolver.RootCauseMessageViolationResolver;

/**
 * PostgreSQL based constraint violation resolver.
 *
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class PostgresViolationResolver extends RootCauseMessageViolationResolver {

    /* Provided: column name */
    private static final Pattern CANNOT_BE_NULL = Pattern.compile("ERROR: null value in column \"(.+)\" violates not-null constraint");
    /* Provided: column definition (including length) */
    private static final Pattern LENGTH_EXCEEDED = Pattern.compile("ERROR: value too long for type (.+)");
    /* Provided: source table name, constraint name, column name, value, target table name */
    private static final Pattern FOREIGN_KEY_VIOLATION = Pattern.compile(
            "ERROR: insert or update on table \"(.+)\" violates foreign key constraint \"(.+)\"\\s+"
            + "Detail: Key \\((.+)\\)=\\((.+)\\) is not present in table \"(.+)\"\\.");
    /* Provided: constraint name, column name, value */
    private static final Pattern UNIQUE_VIOLATION = Pattern
            .compile("ERROR: duplicate key value violates unique constraint \"(.+)\"\\s+Detail: Key \\((.+)\\)=\\((.+)\\) already exists\\.");
    /* Provided: table name, constraint name */
    private static final Pattern CHECK_FAILED = Pattern.compile("ERROR: new row for relation \"(.+)\" violates check constraint \"(.+)\"");
    /* Provided: column name, column type, value type */
    private static final Pattern INVALID_TYPE = Pattern.compile("ERROR: column \"(.+)\" is of type (.+) but expression is of type (.+)\\nHint: .*");

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("NestedAssignment")
    @Override
    protected DatabaseConstraintViolation resolveByMessage(String message) {

        DatabaseConstraintViolation violation = null;

        Matcher matcher;
        if ((matcher = CANNOT_BE_NULL.matcher(message)).matches()) {
            violation = resolveNotNullViolation(matcher);
        } else if ((matcher = LENGTH_EXCEEDED.matcher(message)).matches()) {
            violation = resolveLengthViolation(matcher);
        } else if ((matcher = FOREIGN_KEY_VIOLATION.matcher(message)).matches()) {
            violation = resolveForeignKeyViolation(matcher);
        } else if ((matcher = UNIQUE_VIOLATION.matcher(message)).matches()) {
            violation = resolveUniqueKeyViolation(matcher);
        } else if ((matcher = CHECK_FAILED.matcher(message)).matches()) {
            violation = resolveCheckViolation(matcher);
        } else if ((matcher = INVALID_TYPE.matcher(message)).matches()) {
            violation = resolveTypeViolation(matcher);
        }
        return violation;
    }

    private DatabaseConstraintViolation resolveNotNullViolation(Matcher matcher) {

        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.NOT_NULL);
        violationBuilder.column(matcher.group(1));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveLengthViolation(Matcher matcher) {

        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.LENGTH_EXCEEDED);
        final String columnDefinition = matcher.group(1); // For example: varchar(255)
        String columnType = StringUtils.substringBefore(columnDefinition, "(");
        violationBuilder.expectedType(columnType);
        String columnLength = StringUtils.substringBetween(columnDefinition, "(", ")");
        violationBuilder.maximumLength(Long.valueOf(columnLength));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveForeignKeyViolation(Matcher matcher) {

        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.FOREIGN_KEY);
        violationBuilder.table(matcher.group(1));
        violationBuilder.constraint(matcher.group(2));
        violationBuilder.column(matcher.group(3));
        violationBuilder.value(matcher.group(4));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveUniqueKeyViolation(Matcher matcher) {

        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.UNIQUE_KEY);
        violationBuilder.constraint(matcher.group(1));
        violationBuilder.column(matcher.group(2));
        violationBuilder.value(matcher.group(3));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveCheckViolation(Matcher matcher) {

        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.CHECK_FAILED);
        violationBuilder.table(matcher.group(1));
        violationBuilder.constraint(matcher.group(2));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveTypeViolation(Matcher matcher) {

        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.INVALID_TYPE);
        violationBuilder.column(matcher.group(1));
        violationBuilder.expectedType(matcher.group(2));
        violationBuilder.valueType(matcher.group(3));
        return violationBuilder.build();
    }
}
