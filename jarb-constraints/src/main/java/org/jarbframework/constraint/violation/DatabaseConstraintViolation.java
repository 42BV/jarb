package org.jarbframework.constraint.violation;

import org.jarbframework.utils.StringUtils;

/**
 * Representation of the constraint violation that occurred.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public final class DatabaseConstraintViolation {
    
    private final DatabaseConstraintType constraintType;
    
    private final String constraintName;
    
    private String tableName;
    
    private String columnName;

    private String referencingTableName;
    
    private String referencingColumnName;

    private Object value;
    
    private String valueType;
    
    private String expectedValueType;
        
    private Long maximumLength;
    
    private String statement;
    
    private String number;

    public DatabaseConstraintViolation(DatabaseConstraintType constraintType) {
        this(constraintType, null);
    }
    
    public DatabaseConstraintViolation(String constraintName) {
        this(null, constraintName);
    }
    
    public DatabaseConstraintViolation(DatabaseConstraintType constraintType, String constraintName) {
        if (constraintType == null && StringUtils.isBlank(constraintName)) {
            throw new IllegalArgumentException("Should provide a constraint type or name.");
        }
        
        this.constraintType = constraintType;
        this.constraintName = constraintName;
    }
    
    /**
     * Start building a new {@link DatabaseConstraintViolation}.
     * @param constraintType the type of constraint violation
     * @return new constraint violation builder
     */
    public static Builder builder(DatabaseConstraintType constraintType) {
        return new Builder(constraintType);
    }

    public DatabaseConstraintType getConstraintType() {
        return constraintType;
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

    public String getReferencingColumnName() {
        return referencingColumnName;
    }
    
    public Object getValue() {
        return value;
    }

    public String getValueType() {
        return valueType;
    }

    public String getExpectedValueType() {
        return expectedValueType;
    }

    public Long getMaximumLength() {
        return maximumLength;
    }
    
    public String getStatement() {
        return statement;
    }

    public String getNumber() {
        return number;
    }
    
    /**
     * Component that simplifies building a database constraint violation.
     * @author Jeroen van Schagen
     * @since Aug 5, 2011
     */
    public static final class Builder {
        
        private final DatabaseConstraintType constraintType;
        
        private String constraintName;
        
        private String tableName;
        
        private String columnName;
        
        private String referencingTableName;
        
        private String referencingColumnName;
        
        private Object value;
        
        private String valueType;
        
        private String expectedValueType;
        
        private Long maximumLength;
        
        private String statement;

        private String number;
        
        private Builder(DatabaseConstraintType constraintType) {
            this.constraintType = constraintType;
        }

        public Builder constraint(String constraintName) {
            this.constraintName = constraintName;
            return this;
        }

        public Builder table(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder column(String columnName) {
            this.columnName = columnName;
            return this;
        }
        
        public Builder referencingTable(String referencingTableName) {
            this.referencingTableName = referencingTableName;
            return this;
        }
        
        public Builder referencingColumn(String referencingColumnName) {
            this.referencingColumnName = referencingColumnName;
            return this;
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder valueType(String valueType) {
            this.valueType = valueType;
            return this;
        }

        public Builder expectedValueType(String expectedValueType) {
            this.expectedValueType = expectedValueType;
            return this;
        }
        
        public Builder maximumLength(String maximumLength) {
            return this.maximumLength(Long.valueOf(maximumLength));
        }

        public Builder maximumLength(Long maximumLength) {
            this.maximumLength = maximumLength;
            return this;
        }
        
        public Builder statement(String statement) {
            this.statement = statement;
            return this;
        }
        
        public Builder number(String number) {
            this.number = number;
            return this;
        }

        /**
         * Build the actual constraint violation. Note that the either a constraint type or name
         * must have been declared before building, or an exception will be thrown.
         * @return the database constraint violation
         */
        public DatabaseConstraintViolation build() {
            DatabaseConstraintViolation violation = new DatabaseConstraintViolation(constraintType, constraintName);
            violation.tableName = tableName;
            violation.columnName = columnName;
            violation.referencingTableName = referencingTableName;
            violation.referencingColumnName = referencingColumnName;
            violation.value = value;
            violation.valueType = valueType;
            violation.expectedValueType = expectedValueType;
            violation.maximumLength = maximumLength;
            violation.statement = statement;
            violation.number = number;
            return violation;
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return constraintType + ": " + constraintName;
    }
    
}
