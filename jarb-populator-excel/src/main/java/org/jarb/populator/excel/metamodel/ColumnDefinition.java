package org.jarb.populator.excel.metamodel;

import java.lang.reflect.Field;

import org.springframework.util.Assert;

/**
 * Abstract class containing a columnName, fieldName and a field.
 * @author Willem Eppen
 * @author Sander Benschop
 * 
 */
public class ColumnDefinition {
    private final ColumnType type;
    private final String columnName;
    private Field field;
    private FieldPath embeddablePath;
    private String joinColumnName;
    private String inverseJoinColumnName;
    private boolean generatedValue;

    private ColumnDefinition(String columnName, ColumnType type) {
        Assert.hasText(columnName, "Column name cannot be empty");
        Assert.notNull(type, "Column type cannot be null");
        this.columnName = columnName;
        this.type = type;
    }
    
    public static ColumnDefinition.Builder builder(String columnName) {
        return builder(columnName, ColumnType.BASIC);
    }
    
    public static ColumnDefinition.Builder builder(String columnName, ColumnType type) {
        return new ColumnDefinition.Builder(type).setColumnName(columnName);
    }
    
    public static ColumnDefinition discriminator(String columnName) {
        return new ColumnDefinition(columnName, ColumnType.DISCRIMINATOR);
    }
    
    public ColumnType getType() {
        return type;
    }
    
    public boolean hasField() {
        return field != null;
    }

    public Field getField() {
        return field;
    }
    
    public String getFieldName() {
        return field != null ? field.getName() : null;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isEmbeddedAttribute() {
        return embeddablePath != null && !embeddablePath.isEmpty();
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
        private final ColumnType type;
        private String columnName;
        private Field field;
        private FieldPath embeddablePath;
        private String joinColumnName;
        private String inverseJoinColumnName;
        private boolean generatedValue = false;
        
        public Builder(ColumnType type) {
            this.type = type;
        }
        
        public Builder setColumnName(String columnName) {
            this.columnName = columnName;
            return this;
        }
        
        public Builder valueIsGenerated() {
            generatedValue = true;
            return this;
        }
        
        public Builder setField(Field field) {
            this.field = field;
            return this;
        }
        
        public Builder setEmbeddablePath(FieldPath embeddablePath) {
            this.embeddablePath = embeddablePath;
            return this;
        }
        
        public Builder setJoinColumnName(String joinColumnName) {
            Assert.state(type == ColumnType.JOIN_TABLE, "Can only configure a join column name on a join table column.");
            this.joinColumnName = joinColumnName;
            return this;
        }
        
        public Builder setInverseJoinColumnName(String inverseJoinColumnName) {
            Assert.state(type == ColumnType.JOIN_TABLE, "Can only configure an inverse join column name on a join table column.");
            this.inverseJoinColumnName = inverseJoinColumnName;
            return this;
        }
        
        public ColumnDefinition build() {
            ColumnDefinition definition = new ColumnDefinition(columnName, type);
            definition.field = field;
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
