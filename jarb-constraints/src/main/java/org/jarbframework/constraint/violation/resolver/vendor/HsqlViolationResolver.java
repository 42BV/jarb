package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.builder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.DatabaseConstraintViolationType;
import org.jarbframework.constraint.violation.resolver.RootCauseMessageViolationResolver;
import org.springframework.util.Assert;

/**
 * Hypersonic SQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class HsqlViolationResolver extends RootCauseMessageViolationResolver {

    private static final String CANNOT_BE_NULL_PATTERN
    /* Provided: constraint name, table name, column name */
    = "integrity constraint violation: NOT NULL check constraint; (.+) table: (.+) column: (.+)";

    private static final String UNIQUE_VIOLATION_PATTERN
    /* Provided: constraint name, table name */
    = "integrity constraint violation: unique constraint or index violation; (.+) table: (.+)";

    private static final String FK_VIOLATION_PATTERN
    /* Provided: constraint name, table name */
    = "integrity constraint violation: foreign key no action; (.+) table: (.+)";

    private static final String LENGTH_EXCEEDED_PATTERN
    /* Provided: value type */
    = "data exception: (.+) data, right truncation";

    private static final String INVALID_TYPE_PATTERN
    /* Provided: value type */
    = "data exception: invalid (.+) value for cast";

    /**
     * {@inheritDoc}
     */
    @Override
    protected DatabaseConstraintViolation resolveByMessage(String message) {
        DatabaseConstraintViolation violation = null;
        if (message.matches(CANNOT_BE_NULL_PATTERN)) {
            violation = resolveNotNullViolation(message);
        } else if (message.matches(UNIQUE_VIOLATION_PATTERN)) {
            violation = resolveUniqueViolation(message);
        } else if (message.matches(FK_VIOLATION_PATTERN)) {
            violation = resolveForeignKeyViolation(message);
        } else if (message.matches(LENGTH_EXCEEDED_PATTERN)) {
            violation = resolveLengthViolation(message);
        } else if (message.matches(INVALID_TYPE_PATTERN)) {
            violation = resolveTypeViolation(message);
        }
        return violation;
    }

    private DatabaseConstraintViolation resolveForeignKeyViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = builder(DatabaseConstraintViolationType.FOREIGN_KEY);
        Matcher matcher = Pattern.compile(FK_VIOLATION_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.constraint(matcher.group(1).toLowerCase());
        violationBuilder.table(matcher.group(2).toLowerCase());
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveNotNullViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = builder(DatabaseConstraintViolationType.NOT_NULL);
        Matcher matcher = Pattern.compile(CANNOT_BE_NULL_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.constraint(matcher.group(1).toLowerCase());
        violationBuilder.table(matcher.group(2).toLowerCase());
        violationBuilder.column(matcher.group(3).toLowerCase());
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveUniqueViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = builder(DatabaseConstraintViolationType.UNIQUE_KEY);
        Matcher matcher = Pattern.compile(UNIQUE_VIOLATION_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.constraint(matcher.group(1).toLowerCase());
        violationBuilder.table(matcher.group(2).toLowerCase());
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveLengthViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = builder(DatabaseConstraintViolationType.LENGTH_EXCEEDED);
        Matcher matcher = Pattern.compile(LENGTH_EXCEEDED_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.valueType(matcher.group(1).toLowerCase());
        return violationBuilder.build();
    }

    private DatabaseConstraintViolation resolveTypeViolation(String message) {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = builder(DatabaseConstraintViolationType.INVALID_TYPE);
        Matcher matcher = Pattern.compile(INVALID_TYPE_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.valueType(matcher.group(1).toLowerCase());
        return violationBuilder.build();
    }

}
