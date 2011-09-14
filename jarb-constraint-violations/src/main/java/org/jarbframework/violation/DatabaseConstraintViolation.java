package org.jarbframework.violation;

import static org.jarbframework.utils.Asserts.notNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Representation of the constraint violation that occurred.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public final class DatabaseConstraintViolation {
    private final DatabaseConstraintViolationType violationType;
    private String constraintName;
    private String tableName;
    private String columnName;

    private Object value;
    private String valueType;
    private String expectedType;
    private Long maximumLength;

    private DatabaseConstraintViolation(DatabaseConstraintViolationType violationType) {
        this.violationType = violationType;
    }

    /**
     * Start building a new {@link DatabaseConstraintViolation}.
     * @param violationType the type of constraint violation
     * @return new constraint violation builder
     */
    public static DatabaseConstraintViolationBuilder violation(DatabaseConstraintViolationType violationType) {
        return new DatabaseConstraintViolationBuilder(violationType);
    }

    public DatabaseConstraintViolationType getViolationType() {
        return violationType;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getValue() {
        return value;
    }

    public String getValueType() {
        return valueType;
    }

    public String getExpectedType() {
        return expectedType;
    }

    public Long getMaximumLength() {
        return maximumLength;
    }

    /**
     * Capable of build new violation instances.
     *
     * @author Jeroen van Schagen
     * @date Aug 5, 2011
     */
    public static final class DatabaseConstraintViolationBuilder {
        private final DatabaseConstraintViolationType type;

        private String constraintName;
        private String tableName;
        private String columnName;

        private Object value;
        private String valueType;
        private String expectedType;
        private Long maximumLength;

        private DatabaseConstraintViolationBuilder(DatabaseConstraintViolationType type) {
            this.type = notNull(type, "Violation type cannot be null");
        }

        public DatabaseConstraintViolationBuilder named(String constraintName) {
            this.constraintName = constraintName;
            return this;
        }

        public DatabaseConstraintViolationBuilder table(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public DatabaseConstraintViolationBuilder column(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public DatabaseConstraintViolationBuilder value(Object value) {
            this.value = value;
            return this;
        }

        public DatabaseConstraintViolationBuilder valueType(String valueType) {
            this.valueType = valueType;
            return this;
        }

        public DatabaseConstraintViolationBuilder expectedType(String expectedType) {
            this.expectedType = expectedType;
            return this;
        }

        public DatabaseConstraintViolationBuilder maximumLength(Long maximumLength) {
            this.maximumLength = maximumLength;
            return this;
        }

        public DatabaseConstraintViolation build() {
            DatabaseConstraintViolation violation = new DatabaseConstraintViolation(type);
            violation.constraintName = constraintName;
            violation.tableName = tableName;
            violation.columnName = columnName;
            violation.value = value;
            violation.valueType = valueType;
            violation.expectedType = expectedType;
            violation.maximumLength = maximumLength;
            return violation;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
