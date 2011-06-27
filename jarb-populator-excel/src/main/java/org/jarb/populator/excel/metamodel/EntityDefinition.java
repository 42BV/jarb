package org.jarb.populator.excel.metamodel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * Describes a specific persistable class, providing additional information about
 * the class and its mapping to a database table.
 * 
 * @param <T> type of class being described
 * 
 * @author Willem Eppen
 * @author Sander Benschop
 * @author Jeroen van Schagen
 */
public class EntityDefinition<T> {
    /** Entity class being described. */
    private final Class<T> entityClass;
    
    /** Name of the discriminator column. **/
    private String discriminatorColumnName;
    /** Mapping of each subclass and the related discriminator value. */
    private Map<String, Class<? extends T>> subClasses;
    
    /** Name of the mapped database table. */
    private String tableName;
    
    /** Description of each defined property. */
    private Set<PropertyDefinition> propertyDefinitions;
    
    /**
     * Construct a new {@link ClassDefinition).
     * @param entityClass class being described
     */
    private EntityDefinition(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    /**
     * Start building a new {@link EntityDefinition}.
     * @param <T> type of class being described
     * @param entityClass class being described
     * @return class definition builder
     */
    public static <T> Builder<T> forClass(Class<T> entityClass) {
        return new Builder<T>(entityClass);
    }

    /**
     * Returns the persistentClass belonging to classDefinition.
     * @return persistentClass instance from domain package
     */
    public Class<T> getEntityClass() {
        return entityClass;
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
        for(Map.Entry<String, Class<? extends T>> subClassesEntry : subClasses.entrySet()) {
            if(subClassesEntry.getValue().isAssignableFrom(entitySubClass)) {
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

    /**
     * Retrieve all property definitions declared inside this class.
     * @return definition of each declared property
     */
    public Set<PropertyDefinition> properties() {
        return Collections.unmodifiableSet(propertyDefinitions);
    }

    /**
     * Retrieve a specific property definition.
     * @param propertyName name of the property field
     * @return matching property field, if any
     */
    public PropertyDefinition property(String propertyName) {
        PropertyDefinition result = null;
        for (PropertyDefinition property : propertyDefinitions) {
            if (StringUtils.equalsIgnoreCase(propertyName, property.getName())) {
                result = property;
            }
        }
        return result;
    }

    /**
     * Retrieve a specific property definition, based on property. Note
     * that the column must actually map to a property inside our class.
     * Certain columns, such as discriminators, are not mapped to a field
     * and thus will result in a {@link null}.
     * @param columnName name of the column
     * @return matching property field, if any
     */
    public PropertyDefinition propertyByColumnName(String columnName) {
        PropertyDefinition result = null;
        for (PropertyDefinition property : propertyDefinitions) {
            if (StringUtils.equalsIgnoreCase(columnName, property.getColumnName())) {
                result = property;
            }
        }
        return result;
    }
    
    /**
     * Retrieve all column names for this entity table.
     * @return each column name on this entity
     */
    public Set<String> getColumnNames() {
        Set<String> columnNames = new HashSet<String>();
        for (PropertyDefinition property : propertyDefinitions) {
            if(property.hasColumn()) {
                columnNames.add(property.getColumnName());
            }
        }
        if(hasDiscriminatorColumn()) {
            columnNames.add(discriminatorColumnName);
        }
        return columnNames;
    }

    /**
     * Returns the tableName of the classDefinition.
     * @return tableName String
     */
    public String getTableName() {
        return tableName;
    }
    
    /**
     * Capable of building {@link EntityDefinition} instances.
     * 
     * @author Jeroen van Schagen
     * @since 15-06-2011
     *
     * @param <T> type of class being described
     */
    public static class Builder<T> {
        private final Class<T> persistentClass;
        private String discriminatorColumnName;
        private Map<String, Class<? extends T>> subClasses = new HashMap<String, Class<? extends T>>();
        private String tableName;
        private Set<PropertyDefinition> properties = new HashSet<PropertyDefinition>();
        
        /**
         * Construct a new {@link Builder}.
         * @param entityClass class being described
         */
        public Builder(Class<T> entityClass) {
            Assert.notNull(entityClass, "Entity class cannot be null");
            this.persistentClass = entityClass;
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
         * Include a persistent sub class to the definition.
         * @param discriminatorValue discriminator value of subclass
         * @param persistentSubClass actual persistent subclass
         * @return this for method chaining
         */
        public Builder<T> includeSubClass(String discriminatorValue, Class<? extends T> persistentSubClass) {
            Assert.hasText(discriminatorValue, "Discriminator value cannot be blank");
            Assert.notNull(persistentClass, "Persistent sub class cannot be null");
            subClasses.put(discriminatorValue, persistentSubClass);
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
         * Include a column definition.
         * @param properties property definitions being added
         * @return this for method chaining
         */
        public Builder<T> includeProperties(Collection<PropertyDefinition> properties) {
            this.properties.addAll(properties);
            return this;
        }
        
        /**
         * Construct a new class definition that contains all previously configured attributes.
         * @return new class definition
         */
        public EntityDefinition<T> build() {
            Assert.hasText(tableName, "Table name cannot be blank");

            EntityDefinition<T> classDefinition = new EntityDefinition<T>(persistentClass);
            classDefinition.discriminatorColumnName = discriminatorColumnName;
            classDefinition.subClasses = Collections.unmodifiableMap(subClasses);
            classDefinition.tableName = tableName;
            classDefinition.propertyDefinitions = Collections.unmodifiableSet(properties);
            return classDefinition;
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
        if(obj instanceof EntityDefinition) {
            return entityClass.equals(((EntityDefinition<?>) obj).entityClass);
        } else {
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return entityClass.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return entityClass.getName();
    }

}
