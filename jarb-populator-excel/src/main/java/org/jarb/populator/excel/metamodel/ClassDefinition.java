package org.jarb.populator.excel.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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
public class ClassDefinition<T> {
    /** Persistent class being described. */
    private final Class<T> persistentClass;
    
    /** Name of the discriminator column. **/
    private String discriminatorColumnName;
    /** Mapping of each subclass and the related discriminator value. */
    private Map<String, Class<? extends T>> subClasses;
    
    /** Name of the mapped database table. */
    private String tableName;
    
    /** Description of each defined property. */
    private List<PropertyDefinition> propertyDefinitions;
    
    /**
     * Construct a new {@link ClassDefinition).
     * @param persistentClass class being described
     */
    private ClassDefinition(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }
    
    /**
     * Start building a new {@link ClassDefinition}.
     * @param <T> type of class being described
     * @param persistentClass class being described
     * @return class definition builder
     */
    public static <T> Builder<T> forClass(Class<T> persistentClass) {
        return new Builder<T>(persistentClass);
    }

    /**
     * Returns the persistentClass belonging to classDefinition.
     * @return persistentClass instance from domain package
     */
    public Class<T> getEntityClass() {
        return persistentClass;
    }
    
    /**
     * Retrieve the subclass for a specific discriminator value.
     * @param discriminatorValue discriminator value
     * @return subclass matching our discriminator, or {@code null}
     */
    public Class<? extends T> getSubClass(String discriminatorValue) {
        return subClasses.get(discriminatorValue);
    }
    
    /**
     * Retrieve the discriminator value used to indicate a specific subclass.
     * @param subClass type of subclass for which we retrieve the discriminator
     * @return discriminator value used to indicate the subclass
     */
    public String getDiscriminatorValue(Class<?> subClass) {
        String result = null;
        for(Map.Entry<String, Class<? extends T>> subClassesEntry : subClasses.entrySet()) {
            if(subClassesEntry.getValue().isAssignableFrom(subClass)) {
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
    public List<PropertyDefinition> getPropertyDefinitions() {
        return Collections.unmodifiableList(propertyDefinitions);
    }

    /**
     * Retrieve a specific property definition.
     * @param fieldName name of the property field
     * @return matching property field, if any
     */
    public PropertyDefinition getPropertyDefinition(String fieldName) {
        PropertyDefinition result = null;
        for (PropertyDefinition columnDefinition : propertyDefinitions) {
            if (StringUtils.equalsIgnoreCase(fieldName, columnDefinition.getName())) {
                result = columnDefinition;
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
    public PropertyDefinition getPropertyDefinitionByColumn(String columnName) {
        PropertyDefinition result = null;
        for (PropertyDefinition columnDefinition : propertyDefinitions) {
            if (StringUtils.equalsIgnoreCase(columnName, columnDefinition.getColumnName())) {
                result = columnDefinition;
            }
        }
        return result;
    }
    
    /**
     * Retrieve all column names for this entity table.
     * @return
     */
    public Set<String> getColumnNames() {
        Set<String> columnNames = new HashSet<String>();
        for (PropertyDefinition propertyDefinition : propertyDefinitions) {
            if(propertyDefinition.hasColumn()) {
                columnNames.add(propertyDefinition.getColumnName());
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
     * Capable of building {@link ClassDefinition} instances.
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
        private Set<PropertyDefinition> columnDefinitionSet = new LinkedHashSet<PropertyDefinition>();
        
        /**
         * Construct a new {@link Builder}.
         * @param persistentClass class being described
         */
        public Builder(Class<T> persistentClass) {
            Assert.notNull(persistentClass, "Persistent class cannot be null");
            this.persistentClass = persistentClass;
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
         * @param columnDefinition column definition being included
         * @return this for method chaining
         */
        public Builder<T> includeColumns(Collection<PropertyDefinition> columnDefinitions) {
            columnDefinitionSet.addAll(columnDefinitions);
            return this;
        }
        
        /**
         * Construct a new class definition that contains all previously configured attributes.
         * @return new class definition
         */
        public ClassDefinition<T> build() {
            Assert.hasText(tableName, "Table name cannot be blank");

            ClassDefinition<T> classDefinition = new ClassDefinition<T>(persistentClass);
            classDefinition.discriminatorColumnName = discriminatorColumnName;
            classDefinition.subClasses = Collections.unmodifiableMap(subClasses);
            classDefinition.tableName = tableName;
            final List<PropertyDefinition> columnDefinitionList = new ArrayList<PropertyDefinition>(columnDefinitionSet);
            classDefinition.propertyDefinitions = Collections.unmodifiableList(columnDefinitionList);
            return classDefinition;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return persistentClass.getName();
    }

}
