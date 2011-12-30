package org.jarbframework.populator.excel.metamodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * Describes a specific persistable class, providing additional information about
 * the class and its mapping to a database table.
 * @author Willem Eppen
 * @author Sander Benschop
 * @author Jeroen van Schagen
 * 
 * @param <T> type of class being described
 * 
 */
public class EntityDefinition<T> extends Definition {

    /** Name of the discriminator column. **/
    private String discriminatorColumnName;
    /** Mapping of each subclass and the related discriminator value. */
    private Map<String, Class<? extends T>> subClasses;
    
    /** Entity class being described. */
    protected final Class<T> definedClass;
    
    /**
     * Construct a new {@link EntityDefinition).
     * @param definedClass class being described
     */
    private EntityDefinition(Class<T> entityClass) {
        this.definedClass = entityClass;
    }

    /**
     * Returns the definedClass belonging to classDefinition.
     * @return definedClass instance from domain package
     */
    public Class<T> getDefinedClass() {
        return definedClass;
    }
    
    /**
     * Start building a new {@link EntityDefinition}.
     * @param <T> type of class being described
     * @param definedClass class being described
     * @return class definition builder
     */
    public static <T> Builder<T> forClass(Class<T> entityClass) {
        return new EntityDefinition.Builder<T>(entityClass);
    }

    /**
     * Retrieve the subclass for a specific discriminator value.
     * @param discriminatorValue discriminator value
     * @return subclass matching our discriminator, or {@code null}
     */
    public Class<? extends T> getEntitySubClass(String discriminatorValue) {
        return subClasses.get(discriminatorValue);
    }

    /**
     * Retrieve the discriminator value used to indicate a specific subclass.
     * @param entitySubClass type of subclass for which we retrieve the discriminator
     * @return discriminator value used to indicate the subclass
     */
    public String getDiscriminatorValue(Class<?> entitySubClass) {
        String result = null;
        for (Map.Entry<String, Class<? extends T>> subClassesEntry : subClasses.entrySet()) {
            if (subClassesEntry.getValue().isAssignableFrom(entitySubClass)) {
                result = subClassesEntry.getKey();
                break; // Result found, quit searching
            }
        }
        return result;
    }

    /** 
     * Returns the discriminator's column name.
     * @return The discriminator column's column name 
     */
    public String getDiscriminatorColumnName() {
        return discriminatorColumnName;
    }

    /**
     * Determine if a class has a discriminator column.
     * @return {@code true} if it has, else {@code false}
     */
    public boolean hasDiscriminatorColumn() {
        return StringUtils.isNotBlank(discriminatorColumnName);
    }

    @Override
    public Set<String> getColumnNames() {
        Set<String> columnNames = new HashSet<String>();
        columnNames.addAll(super.getColumnNames());

        if (hasDiscriminatorColumn()) {
            columnNames.add(discriminatorColumnName);
        }

        return columnNames;
    }

    /**
     * Returns the column name of the field annotated with @Id in the entity class.
     * @return Column name of Id column.
     */
    public String getIdColumnName() {
        String columnName = null;
        for (PropertyDefinition propertyDefinition : this.properties()) {
            if (propertyDefinition.isIdColumn()) {
                return propertyDefinition.getColumnName();
            }
        }
        return columnName;
    }

    public static class Builder<T> extends Definition.Builder<T> {
        /** Name of the discriminator column. **/
        private String discriminatorColumnName;

        protected final Class<T> definedClass;
        
        /** Mapping of each subclass and the related discriminator value. */
        private Map<String, Class<? extends T>> subClasses = new HashMap<String, Class<? extends T>>();

        /**
         * Construct a new {@link Builder}.
         * @param definedClass class being described
         */
        public Builder(Class<T> entityClass) {
            this.definedClass = entityClass;
        }

        /**
         * Construct a new class definition that contains all previously configured attributes.
         * @return new class definition
         */
        public EntityDefinition<T> build() {
            Assert.hasText(tableName, "Table name cannot be blank");

            EntityDefinition<T> classDefinition = new EntityDefinition<T>(definedClass);
            classDefinition.discriminatorColumnName = discriminatorColumnName;
            classDefinition.subClasses = Collections.unmodifiableMap(subClasses);
            classDefinition.tableName = tableName;
            classDefinition.propertyDefinitions = Collections.unmodifiableSet(properties);
            return classDefinition;
        }

        /**
         * Describe the discriminator column name.
         * @param discriminatorColumnName discriminator column name
         * @return this for method chaining
         */
        public Builder<T> setDiscriminatorColumnName(final String discriminatorColumnName) {
            this.discriminatorColumnName = discriminatorColumnName;
            return this;
        }

        /**
         * Describe the table name of our class.
         * @param tableName name of the table
         * @return this for method chaining
         */
        public Builder<T> setTableName(final String tableName) {
            this.tableName = tableName;
            return this;
        }

        /**
         * Include a persistent sub class to the definition.
         * @param discriminatorValue discriminator value of subclass
         * @param persistentSubClass actual persistent subclass
         * @return this for method chaining
         */
        public Builder<T> includeSubClass(String discriminatorValue, Class<? extends T> persistentSubClass) {
            Assert.hasText(discriminatorValue, "Discriminator value cannot be blank");
            Assert.notNull(definedClass, "Persistent sub class cannot be null");
            subClasses.put(discriminatorValue, persistentSubClass);
            return this;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof EntityDefinition<?>) {
            return definedClass.equals(((EntityDefinition<?>) obj).definedClass);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return definedClass.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return definedClass.getName();
    }

}
