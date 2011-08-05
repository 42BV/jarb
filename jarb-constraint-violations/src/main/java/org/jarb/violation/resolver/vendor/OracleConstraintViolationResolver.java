package org.jarb.violation.resolver.vendor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationType;
import org.jarb.violation.resolver.RootCauseMessageConstraintViolationResolver;
import org.springframework.util.Assert;

/**
 * Oracle based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class OracleConstraintViolationResolver extends RootCauseMessageConstraintViolationResolver {

    private static final String CHECK_FAILED_PATTERN
    /* Provided: schema and check name */
    = "(.+): check constraint \\((.+)\\.(.+)\\) violated";

    private static final String CANNOT_BE_NULL_PATTERN
    /* Provided: schema, table and column name */
    = "(.+): cannot insert NULL into \\(\"(.+)\"\\.\"(.+)\"\\.\"(.+)\"\\)";

    private static final String UNIQUE_VIOLATION_PATTERN
    /* Provided: schema and constraint name */
    = "(.+): unique constraint \\((.+)\\.(.+)\\) violated";

    private static final String FK_VIOLATION_PATTERN
    /* Provided: schema and constraint name */
    = "(.+): integrity constraint \\((.+)\\.(.+)\\) violated - child record found";

    private static final String LENGTH_EXCEEDED_PATTERN
    /* Provided: schema, table and column name, actual length, maximum length */
    = "(.+): value too large for column \"(.+)\"\\.\"(.+)\"\\.\"(.+)\" \\(actual: (\\d+), maximum: (\\d+)\\)";

    private static final String INVALID_TYPE_PATTERN
    /* Provided: column type */
    = "(.+): invalid (.+)";

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
        } else if (message.matches(FK_VIOLATION_PATTERN)) {
            violation = resolveForeignKeyViolation(message);
        } else if (message.matches(LENGTH_EXCEEDED_PATTERN)) {
            violation = resolveLengthViolation(message);
        } else if (message.matches(INVALID_TYPE_PATTERN)) {
            violation = resolveTypeViolation(message);
        }
        return violation;
    }

    private ConstraintViolation resolveCheckViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.CHECK_FAILED);
        Matcher matcher = Pattern.compile(CHECK_FAILED_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setConstraintName(matcher.group(3));
        return violationBuilder.build();
    }

    private ConstraintViolation resolveUniqueKeyViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.UNIQUE);
        Matcher matcher = Pattern.compile(UNIQUE_VIOLATION_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setConstraintName(matcher.group(3));
        return violationBuilder.build();
    }

    private ConstraintViolation resolveForeignKeyViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.FOREIGN_KEY);
        Matcher matcher = Pattern.compile(FK_VIOLATION_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setConstraintName(matcher.group(3));
        return violationBuilder.build();
    }

    private ConstraintViolation resolveNotNullViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.NOT_NULL);
        Matcher matcher = Pattern.compile(CANNOT_BE_NULL_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setTableName(matcher.group(3));
        violationBuilder.setColumnName(matcher.group(4));
        return violationBuilder.build();
    }

    private ConstraintViolation resolveLengthViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.LENGTH_EXCEEDED);
        Matcher matcher = Pattern.compile(LENGTH_EXCEEDED_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setTableName(matcher.group(3));
        violationBuilder.setColumnName(matcher.group(4));
        violationBuilder.setMaximumLength(Long.valueOf(matcher.group(6)));
        return violationBuilder.build();
    }

    private ConstraintViolation resolveTypeViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.INVALID_TYPE);
        Matcher matcher = Pattern.compile(INVALID_TYPE_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setExpectedType(matcher.group(2));
        return violationBuilder.build();
    }
}
