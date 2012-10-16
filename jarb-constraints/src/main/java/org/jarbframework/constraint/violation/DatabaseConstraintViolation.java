package org.jarbframework.constraint.violation;

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

    private String referencingTableName;
    private Object value;
    private String valueType;
    private String expectedType;
    private Long maximumLength;

    public DatabaseConstraintViolation(DatabaseConstraintViolationType violationType) {
        this.violationType = violationType;
    }
    
    public DatabaseConstraintViolation(DatabaseConstraintViolationType violationType, String constraintName) {
        this.violationType = violationType;
    }

    /**
     * Start building a new {@link DatabaseConstraintViolation}.
     * @param violationType the type of constraint violation
     * @return new constraint violation builder
     */
    public static DatabaseConstraintViolationBuilder builder(DatabaseConstraintViolationType violationType) {
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
    
    public String getReferencingTableName() {
        return referencingTableName;
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
     * Component that simplifies building a database constraint violation.
     * @author Jeroen van Schagen
     * @date Aug 5, 2011
     */
    public static final class DatabaseConstraintViolationBuilder {
        
        private final DatabaseConstraintViolationType violationType;

        private String constraintName;
        private String tableName;
        private String columnName;

        private String referencingTableName;
        private Object value;
        private String valueType;
        private String expectedType;
        private Long maximumLength;

        private DatabaseConstraintViolationBuilder(DatabaseConstraintViolationType violationType) {
            this.violationType = notNull(violationType, "Violation type cannot be null");
        }

        public DatabaseConstraintViolationBuilder constraint(String constraintName) {
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
        
        public DatabaseConstraintViolationBuilder referencingTable(String referencingTableName) {
            this.referencingTableName = referencingTableName;
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
            DatabaseConstraintViolation constraintViolation = new DatabaseConstraintViolation(violationType);
            constraintViolation.constraintName = constraintName;
            constraintViolation.tableName = tableName;
            constraintViolation.columnName = columnName;
            constraintViolation.referencingTableName = referencingTableName;
            constraintViolation.value = value;
            constraintViolation.valueType = valueType;
            constraintViolation.expectedType = expectedType;
            constraintViolation.maximumLength = maximumLength;
            return constraintViolation;
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
