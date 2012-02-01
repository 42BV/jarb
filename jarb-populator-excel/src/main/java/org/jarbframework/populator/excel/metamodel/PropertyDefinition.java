package org.jarbframework.populator.excel.metamodel;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.Assert;

/**
 * Describes a property and its mapping to the database.
 * 
 * @author Willem Eppen
 * @author Sander Benschop
 */
public final class PropertyDefinition {
    private final Field field;
    private String columnName;
    private PropertyDatabaseType databaseType;
    private PropertyPath embeddablePath;
    private boolean generatedValue;
    private boolean isIdColumn;
    private String joinTableName;
    private String joinColumnName;
    private String inverseJoinColumnName;
    private InverseJoinColumnReferenceProperties inverseJoinColumnReferenceProperties;

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
        return databaseType != PropertyDatabaseType.COLLECTION_REFERENCE;
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

    public boolean isIdColumn() {
        return isIdColumn;
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
        private InverseJoinColumnReferenceProperties inverseJoinColumnReferenceProperties;

        private Map<String, String> elementCollectionJoinColumns;
        private boolean generatedValue = false;
        private boolean isIdColumn = false;

        public Builder(Field field) {
            Assert.notNull(field, "Field cannot be null");
            this.field = field;
            this.elementCollectionJoinColumns = new HashMap<String, String>();
        }

        public Builder setColumnName(String databaseColumnName) {
            this.columnName = databaseColumnName;
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

        public Builder columnIsIdColumn() {
            isIdColumn = true;
            return this;
        }

        public Builder setEmbeddablePath(PropertyPath embeddablePropertyPath) {
            this.embeddablePath = embeddablePropertyPath;
            return this;
        }

        public Builder setJoinTableName(String databaseJoinTableName) {
            this.joinTableName = databaseJoinTableName;
            return this;
        }

        public Builder setJoinColumnName(String databaseJoinColumnName) {
            this.joinColumnName = databaseJoinColumnName;
            return this;
        }

        public Builder setInverseJoinColumnName(String databaseInverseJoinColumnName) {
            this.inverseJoinColumnName = databaseInverseJoinColumnName;
            return this;
        }

        /**
         * Sets the InverseJoinColumnReferenceProperties data structure, which holds information needed when coupling @ElementCollection tables 
         * or @OneToMany tables with an @JoinColumn annotation on it.
         * @param referenceProperties InverseJoinColumnReferenceProperties data structure
         * @return This builder for method chaining
         */
        public Builder setInverseJoinColumnReferenceProperties(InverseJoinColumnReferenceProperties referenceProperties) {
            this.inverseJoinColumnReferenceProperties = referenceProperties;
            return this;
        }

        public PropertyDefinition build() {
            Assert.notNull(databaseType, "Database type cannot be null");
            if (databaseType == PropertyDatabaseType.COLLECTION_REFERENCE) {
                Assert.state(isBlank(columnName), "Join table property cannot have a column name");
                Assert.state(elementCollectionJoinColumns.size() == 0, "Join table property cannot have elementCollection joinColumn names");
                Assert.state(isNotBlank(joinTableName), "Join table name cannot be blank");
                Assert.state(isNotBlank(joinColumnName), "Join column name cannot be blank");
                Assert.state(isNotBlank(inverseJoinColumnName), "Inverse join column name cannot be blank");
            } else if (databaseType == PropertyDatabaseType.INVERSED_REFERENCE) {
                Assert.state(isBlank(columnName), "Element collection property cannot have a column name");
                Assert.state(isBlank(inverseJoinColumnName), "Element collection property cannot have an inversed joinColumn name");
                Assert.state(isBlank(joinTableName), "ElementCollection property cannot have a joinTable name");
                Assert.state(isBlank(joinColumnName), "ElementCollection property cannot have a join column name");
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
            definition.isIdColumn = isIdColumn;
            definition.inverseJoinColumnReferenceProperties = inverseJoinColumnReferenceProperties;
            return definition;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PropertyDefinition) {
            boolean equal = field.equals(((PropertyDefinition) obj).field);
            if (isEmbeddedAttribute()) {
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

    /**
     * Returns the InverseJoinColumnReferenceProperties data structure, which holds information needed when coupling @ElementCollection tables 
     * or @OneToMany tables with an @JoinColumn annotation on it.
     * @return InverseJoinColumnReferenceProperties data structure
     */
    public InverseJoinColumnReferenceProperties getInverseJoinColumnReferenceProperties() {
        return inverseJoinColumnReferenceProperties;
    }

}
