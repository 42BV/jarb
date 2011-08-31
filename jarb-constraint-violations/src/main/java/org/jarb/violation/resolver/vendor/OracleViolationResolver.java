package org.jarb.violation.resolver.vendor;

import static org.jarb.violation.DatabaseConstraintViolation.violation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jarb.violation.DatabaseConstraintViolation;
import org.jarb.violation.DatabaseConstraintViolationType;
import org.jarb.violation.resolver.RootCauseMessageViolationResolver;
import org.springframework.util.Assert;

/**
 * Oracle based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class OracleViolationResolver extends RootCauseMessageViolationResolver {

    private static final String CHECK_FAILED_PATTERN
    /* Provided: schema and check name */
    = "(.+): check constraint \\((.+)\\.(.+)\\) violated\n";

    private static final String CANNOT_BE_NULL_PATTERN
    /* Provided: schema, table and column name */
    = "(.+): cannot insert NULL into \\(\"(.+)\"\\.\"(.+)\"\\.\"(.+)\"\\)\n";

    private static final String UNIQUE_VIOLATION_PATTERN
    /* Provided: schema and constraint name */
    = "(.+): unique constraint \\((.+)\\.(.+)\\) violated\n";

    private static final String FK_VIOLATION_PATTERN
    /* Provided: schema and constraint name */
    = "(.+): integrity constraint \\((.+)\\.(.+)\\) violated - child record found\n";

    private static final String LENGTH_EXCEEDED_PATTERN
    /* Provided: schema, table and column name, actual length, maximum length */
    = "(.+): value too large for column \"(.+)\"\\.\"(.+)\"\\.\"(.+)\" \\(actual: (\\d+), maximum: (\\d+)\\)\n";

    private static final String INVALID_TYPE_PATTERN
    /* Provided: column type */
    = "(.+): invalid (.+)\n";

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
        } else if (message.matches(FK_VIOLATION_PATTERN)) {
            violation = resolveForeignKeyViolation(message);
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
        violationBuilder.named(matcher.group(3));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveUniqueKeyViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.UNIQUE_KEY);
        Matcher matcher = Pattern.compile(UNIQUE_VIOLATION_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.named(matcher.group(3));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveForeignKeyViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.FOREIGN_KEY);
        Matcher matcher = Pattern.compile(FK_VIOLATION_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.named(matcher.group(3));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveNotNullViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.NOT_NULL);
        Matcher matcher = Pattern.compile(CANNOT_BE_NULL_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.table(matcher.group(3));
        violationBuilder.column(matcher.group(4));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveLengthViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.LENGTH_EXCEEDED);
        Matcher matcher = Pattern.compile(LENGTH_EXCEEDED_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.table(matcher.group(3));
        violationBuilder.column(matcher.group(4));
        violationBuilder.maximumLength(Long.valueOf(matcher.group(6)));
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveTypeViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(DatabaseConstraintViolationType.INVALID_TYPE);
        Matcher matcher = Pattern.compile(INVALID_TYPE_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.expectedType(matcher.group(2));
        return violationBuilder.build();
    }
}
