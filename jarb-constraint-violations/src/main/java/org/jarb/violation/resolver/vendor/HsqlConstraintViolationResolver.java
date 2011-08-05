package org.jarb.violation.resolver.vendor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationType;
import org.jarb.violation.resolver.RootCauseMessageConstraintViolationResolver;
import org.springframework.util.Assert;

/**
 * Hypersonic SQL based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class HsqlConstraintViolationResolver extends RootCauseMessageConstraintViolationResolver {

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
    protected ConstraintViolation resolveByMessage(String message) {
        ConstraintViolation violation = null;
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

    private ConstraintViolation resolveForeignKeyViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.FOREIGN_KEY);
        Matcher matcher = Pattern.compile(FK_VIOLATION_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setConstraintName(matcher.group(1).toLowerCase());
        violationBuilder.setTableName(matcher.group(2).toLowerCase());
        return violationBuilder.build();
    }

    private ConstraintViolation resolveNotNullViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.NOT_NULL);
        Matcher matcher = Pattern.compile(CANNOT_BE_NULL_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setConstraintName(matcher.group(1).toLowerCase());
        violationBuilder.setTableName(matcher.group(2).toLowerCase());
        violationBuilder.setColumnName(matcher.group(3).toLowerCase());
        return violationBuilder.build();
    }

    private ConstraintViolation resolveUniqueViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.UNIQUE);
        Matcher matcher = Pattern.compile(UNIQUE_VIOLATION_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setConstraintName(matcher.group(1).toLowerCase());
        violationBuilder.setTableName(matcher.group(2).toLowerCase());
        return violationBuilder.build();
    }

    private ConstraintViolation resolveLengthViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.LENGTH_EXCEEDED);
        Matcher matcher = Pattern.compile(LENGTH_EXCEEDED_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setValueType(matcher.group(1).toLowerCase());
        return violationBuilder.build();
    }

    private ConstraintViolation resolveTypeViolation(String message) {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.INVALID_TYPE);
        Matcher matcher = Pattern.compile(INVALID_TYPE_PATTERN).matcher(message);
        Assert.isTrue(matcher.matches()); // Retrieve group information
        violationBuilder.setValueType(matcher.group(1).toLowerCase());
        return violationBuilder.build();
    }

}
