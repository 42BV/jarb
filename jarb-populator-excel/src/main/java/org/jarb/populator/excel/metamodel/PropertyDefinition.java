package org.jarb.populator.excel.metamodel;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.lang.reflect.Field;

import org.springframework.util.Assert;

/**
 * Describes a property and its mapping to the database.
 * 
 * @author Willem Eppen
 * @author Sander Benschop
 */
public class PropertyDefinition {
    private final Field field;
    private String columnName;
    private PropertyDatabaseType databaseType;
    private PropertyPath embeddablePath;
    private boolean generatedValue;
    private String joinTableName;
    private String joinColumnName;
    private String inverseJoinColumnName;

    private PropertyDefinition(Field field) {
        this.field = field;
    }
    
    public static PropertyDefinition.Builder forField(Field field) {
        return new PropertyDefinition.Builder(field);
    }
    
    public Field getField() {
        return field;
    }
    
    public String getName() {
        return field.getName();
    }
    
    public Class<?> getJavaType() {
        return field.getType();
    }
    
    public PropertyDatabaseType getDatabaseType() {
        return databaseType;
    }

    public String getColumnName() {
        return columnName;
    }
    
    public boolean hasColumn() {
        // Join table properties have no column name, their content is in another table
        return databaseType != PropertyDatabaseType.JOIN_TABLE;
    }

    public boolean isEmbeddedAttribute() {
        return embeddablePath != null;
    }

    public PropertyPath getEmbeddablePath() {
        return embeddablePath;
    }
    
    public boolean isGeneratedValue() {
        return generatedValue;
    }
    
    public String getJoinTableName() {
        return joinTableName;
    }
    
    public String getJoinColumnName() {
        return joinColumnName;
    }
    
    public String getInverseJoinColumnName() {
        return inverseJoinColumnName;
    }
    
    public static class Builder {
        private final Field field;
        private String columnName;
        private PropertyDatabaseType databaseType = PropertyDatabaseType.COLUMN;
        private PropertyPath embeddablePath;
        private String joinTableName;
        private String joinColumnName;
        private String inverseJoinColumnName;
        private boolean generatedValue = false;
        
        public Builder(Field field) {
            Assert.notNull(field, "Field cannot be null");
            this.field = field;
        }
        
        public Builder setColumnName(String columnName) {
            this.columnName = columnName;
            return this;
        }
        
        public Builder setDatabaseType(PropertyDatabaseType columnType) {
            this.databaseType = columnType;
            return this;
        }
        
        public Builder valueIsGenerated() {
            generatedValue = true;
            return this;
        }
        
        public Builder setEmbeddablePath(PropertyPath embeddablePath) {
            this.embeddablePath = embeddablePath;
            return this;
        }
        
        public Builder setJoinTableName(String joinTableName) {
            this.joinTableName = joinTableName;
            return this;
        }
        
        public Builder setJoinColumnName(String joinColumnName) {
            this.joinColumnName = joinColumnName;
            return this;
        }
        
        public Builder setInverseJoinColumnName(String inverseJoinColumnName) {
            this.inverseJoinColumnName = inverseJoinColumnName;
            return this;
        }
        
        public PropertyDefinition build() {
            Assert.notNull(databaseType, "Database type cannot be null");
            if(databaseType == PropertyDatabaseType.JOIN_TABLE) {
                Assert.state(isBlank(columnName), "Join table property cannot have a column name");
                Assert.state(isNotBlank(joinTableName), "Join table name cannot be blank");
                Assert.state(isNotBlank(joinColumnName), "Join column name cannot be blank");
                Assert.state(isNotBlank(inverseJoinColumnName), "Inverse join column name cannot be blank");
            } else {
                Assert.state(isNotBlank(columnName), "Column name cannot be blank");
            }

            PropertyDefinition definition = new PropertyDefinition(field);
            definition.columnName = columnName;
            definition.databaseType = databaseType;
            definition.embeddablePath = embeddablePath;
            definition.joinTableName = joinTableName;
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
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj instanceof PropertyDefinition) {
            boolean equal = field.equals(((PropertyDefinition) obj).field);
            if(isEmbeddedAttribute()) {
                equal = equal && embeddablePath.equals(((PropertyDefinition) obj).embeddablePath);
            }
            return equal;
        } else {
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return field.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Property: %s (%s)", columnName, getName());
    }

}
