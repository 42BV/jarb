package org.jarb.populator.excel.metamodel;

import java.lang.reflect.Field;

import org.springframework.util.Assert;

/**
 * Describes a property and its mapping to the database.
 * @author Willem Eppen
 * @author Sander Benschop
 */
public class ColumnDefinition {
    private final Field field;
    private String columnName;
    private ColumnType columnType;
    private FieldPath embeddablePath;
    private String joinColumnName;
    private String inverseJoinColumnName;
    private boolean generatedValue;

    private ColumnDefinition(Field field) {
        this.field = field;
    }
    
    public static ColumnDefinition.Builder forField(Field field) {
        return new ColumnDefinition.Builder(field);
    }
    
    public ColumnType getColumnType() {
        return columnType;
    }
    
    public boolean hasField() {
        return field != null;
    }

    public Field getField() {
        return field;
    }
    
    public String getFieldName() {
        return field.getName();
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isEmbeddedAttribute() {
        return embeddablePath != null;
    }

    public FieldPath getEmbeddablePath() {
        return embeddablePath;
    }
    
    public String getJoinColumnName() {
        return joinColumnName;
    }
    
    public String getInverseJoinColumnName() {
        return inverseJoinColumnName;
    }
    
    public boolean isGeneratedValue() {
        return generatedValue;
    }
    
    public static class Builder {
        private final Field field;
        private String columnName;
        private ColumnType columnType = ColumnType.BASIC;
        private FieldPath embeddablePath;
        private String joinColumnName;
        private String inverseJoinColumnName;
        private boolean generatedValue = false;
        
        public Builder(Field field) {
            Assert.notNull(field, "Field cannot be null");
            this.field = field;
            columnName = field.getName();
        }
        
        public Builder setColumnName(String columnName) {
            this.columnName = columnName;
            return this;
        }
        
        public Builder setColumnType(ColumnType columnType) {
            this.columnType = columnType;
            return this;
        }
        
        public Builder valueIsGenerated() {
            generatedValue = true;
            return this;
        }
        
        public Builder setEmbeddablePath(FieldPath embeddablePath) {
            this.embeddablePath = embeddablePath;
            return this;
        }
        
        public Builder setJoinColumnName(String joinColumnName) {
            Assert.state(columnType == ColumnType.JOIN_TABLE, "Can only configure a join column name on a join table column.");
            this.joinColumnName = joinColumnName;
            return this;
        }
        
        public Builder setInverseJoinColumnName(String inverseJoinColumnName) {
            Assert.state(columnType == ColumnType.JOIN_TABLE, "Can only configure an inverse join column name on a join table column.");
            this.inverseJoinColumnName = inverseJoinColumnName;
            return this;
        }
        
        public ColumnDefinition build() {
            Assert.hasText(columnName, "Column name cannot be empty");
            Assert.notNull(columnType, "Column type cannot be null");

            ColumnDefinition definition = new ColumnDefinition(field);
            definition.columnName = columnName;
            definition.columnType = columnType;
            definition.embeddablePath = embeddablePath;
            definition.joinColumnName = joinColumnName;
            definition.inverseJoinColumnName = inverseJoinColumnName;
            definition.generatedValue = generatedValue;
            return definition;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Column: %s (%s)", columnName, getFieldName());
    }

}
