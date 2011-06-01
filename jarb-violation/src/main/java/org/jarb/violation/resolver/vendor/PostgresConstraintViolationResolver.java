package org.jarb.violation.resolver.vendor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationType;
import org.jarb.violation.resolver.RootCauseMessageConstraintViolationResolver;
import org.springframework.util.Assert;

/**
 * PostgreSQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class PostgresConstraintViolationResolver extends RootCauseMessageConstraintViolationResolver {

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
    protected ConstraintViolation resolveByMessage(String message) {
        ConstraintViolation violation = null;
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

    private ConstraintViolation resolveCheckViolation(String message) {
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.CHECK_FAILED);
        Matcher matcher = Pattern.compile(CHECK_FAILED_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violation.setTableName(matcher.group(1));
        violation.setConstraintName(matcher.group(2));
        return violation;
    }

    private ConstraintViolation resolveNotNullViolation(String message) {
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.CANNOT_BE_NULL);
        Matcher matcher = Pattern.compile(CANNOT_BE_NULL_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violation.setColumnName(matcher.group(1));
        return violation;
    }

    private ConstraintViolation resolveUniqueKeyViolation(String message) {
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.UNIQUE_VIOLATION);
        Matcher matcher = Pattern.compile(UNIQUE_VIOLATION_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violation.setConstraintName(matcher.group(1));
        violation.setColumnName(matcher.group(2));
        violation.setValue(matcher.group(3));
        return violation;
    }

    private ConstraintViolation resolveLengthViolation(String message) {
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.LENGTH_EXCEEDED);
        Matcher matcher = Pattern.compile(LENGTH_EXCEEDED_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        final String columnDefinition = matcher.group(1); // For example: varchar(255)
        String columnType = StringUtils.substringBefore(columnDefinition, "(");
        violation.setExpectedType(columnType);
        String columnLength = StringUtils.substringBetween(columnDefinition, "(", ")");
        violation.setMaximumLength(Long.valueOf(columnLength));
        return violation;
    }

    private ConstraintViolation resolveTypeViolation(String message) {
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.INVALID_TYPE);
        Matcher matcher = Pattern.compile(INVALID_TYPE_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violation.setColumnName(matcher.group(1));
        violation.setExpectedType(matcher.group(2));
        violation.setValueType(matcher.group(3));
        return violation;
    }
}
