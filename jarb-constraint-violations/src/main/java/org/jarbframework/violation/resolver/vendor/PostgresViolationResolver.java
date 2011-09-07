package org.jarbframework.violation.resolver.vendor;

import static org.jarbframework.violation.DatabaseConstraintViolation.violation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.DatabaseConstraintViolationType;
import org.jarbframework.violation.resolver.RootCauseMessageViolationResolver;
import org.springframework.util.Assert;

/**
 * PostgreSQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class PostgresViolationResolver extends RootCauseMessageViolationResolver {

    private static final String CHECK_FAILED_PATTERN
    /* Provided: table name, constraint name */
    = "ERROR: new row for relation \"(.+)\" violates check constraint \"(.+)\"";

    private static final String CANNOT_BE_NULL_PATTERN
    /* Provided: column name */
    = "ERROR: null value in column \"(.+)\" violates not-null constraint";

    private static final String UNIQUE_VIOLATION_PATTERN
    /* Provided: constraint name, column name, value */
    = "ERROR: duplicate key value violates unique constraint \"(.+)\" Detail: Key \\((.+)\\)=\\((.+)\\) already exists.";

    private static final String LENGTH_EXCEEDED_PATTERN
    /* Provided: column definition (including length) */
    = "ERROR: value too long for type (.+)";

    private static final String INVALID_TYPE_PATTERN
    /* Provided: column name, column type, value type */
    = "ERROR: column \"(.+)\" is of type (.+) but expression is of type (.+)\\nHint: .*";

    /**
     * {@inheritDoc}
     */
    @Override
    protected DatabaseConstraintViolation resolveByMessage(String message) {
        DatabaseConstraintViolation violation = null;
        if (message.matches(CHECK_FAILED_PATTERN)) {
            violation = resolveCheckViolation(message);
        } else if (message.matches(CANNOT_BE_NULL_PATTERN)) {
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

    private DatabaseConstraintViolation resolveCheckViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.CHECK_FAILED);
        Matcher matcher = Pattern.compile(CHECK_FAILED_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.table(matcher.group(1));
        violationBuilder.named(matcher.group(2));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveNotNullViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.NOT_NULL);
        Matcher matcher = Pattern.compile(CANNOT_BE_NULL_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.column(matcher.group(1));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveUniqueKeyViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.UNIQUE_KEY);
        Matcher matcher = Pattern.compile(UNIQUE_VIOLATION_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.named(matcher.group(1));
        violationBuilder.column(matcher.group(2));
        violationBuilder.value(matcher.group(3));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveLengthViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.LENGTH_EXCEEDED);
        Matcher matcher = Pattern.compile(LENGTH_EXCEEDED_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        final String columnDefinition = matcher.group(1); // For example: varchar(255)
        String columnType = StringUtils.substringBefore(columnDefinition, "(");
        violationBuilder.expectedType(columnType);
        String columnLength = StringUtils.substringBetween(columnDefinition, "(", ")");
        violationBuilder.maximumLength(Long.valueOf(columnLength));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveTypeViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.INVALID_TYPE);
        Matcher matcher = Pattern.compile(INVALID_TYPE_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.column(matcher.group(1));
        violationBuilder.expectedType(matcher.group(2));
        violationBuilder.valueType(matcher.group(3));
        return violationBuilder.build();
    }

}
