package org.jarb.populator.excel.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ClassDefiniton contains all the tabledata which is bound to a certain persistent class from the domain package.
 * @author Willem Eppen
 * @author Sander Benschop
 */
public class ClassDefinition<T> {
    /** Persistent class being described. */
    private final Class<T> persistentClass;
    /** Mapping of each subclass and the related discriminator value. */
    private Map<String, Class<? extends T>> subClasses = new HashMap<String, Class<? extends T>>();
    /** Name of the mapped database table. */
    private String tableName;
    /** Definition of each column in the table. */
    private List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();

    public ClassDefinition(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }
    
    public static <T> ClassDefinition<T> forClass(Class<T> persistentClass) {
        return new ClassDefinition<T>(persistentClass);
    }

    /**
     * Returns the persistentClass belonging to classDefinition.
     * @return persistentClass instance from domain package
     */
    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    /**
     * Adds a columnDefinition to the classDefinition.
     * @param propertyDefinition instance of ColumnDefinition
     */
    public void addPropertyDefinition(ColumnDefinition propertyDefinition) {
        columnDefinitions.add(propertyDefinition);
    }

    /**
     * Adds a whole list of columnDefinitions at once.
     * @param additionalPropertyDefinitions instances of ColumnDefinition
     */
    public void addPropertyDefinitionList(Collection<ColumnDefinition> additionalPropertyDefinitions) {
        for (ColumnDefinition propertyDefinition : additionalPropertyDefinitions) {
            addPropertyDefinition(propertyDefinition);
        }
    }

    /**
     * Returns all the columnDefinitions belonging to the classDefinition.
     * @return set of ColumnDefinitions
     */
    public List<ColumnDefinition> getColumnDefinitions() {
        return Collections.unmodifiableList(columnDefinitions);
    }

    /**
     * Returns a ColumnDefinition that holds the passed fieldname.
     * @param fieldName Fieldname to search the ColumnDefinitions for
     * @return ColumnDefinition
     */
    public ColumnDefinition getColumnDefinitionByFieldName(String fieldName) {
        ColumnDefinition result = null;
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            if (columnDefinition.getFieldName().equals(fieldName)) {
                result = columnDefinition;
            }
        }
        return result;
    }

    /**
     * Returns a ColumnDefinition that holds the passed column name.
     * @param columnName Column name to search the ColumnsDefinitions for
     * @return ColumnDefinition
     */
    public ColumnDefinition getColumnDefinitionByColumnName(String columnName) {
        ColumnDefinition result = null;
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            if (columnDefinition.getColumnName().equals(columnName)) {
                result = columnDefinition;
            }
        }
        return result;
    }
    
    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<String>();
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            columnNames.add(columnDefinition.getColumnName());
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
     * Sets the tableName of the classDefinition.
     * @param tableName String
     */
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    /**
     * Adds a persistent subclass to the ClassDefinition, mapped by its discriminator value. Needed for making proper Excelrecords.
     * @param discriminatorValue Discriminator value of subclass
     * @param persistentSubClass Persistent class of subclass
     */
    public void addSubClass(String discriminatorValue, Class<? extends T> persistentSubClass) {
        subClasses.put(discriminatorValue, persistentSubClass);
    }

    /**
     * Adds the whole subclass map.
     * @param subClassMap Map of subclasses to be added
     */
    public void addSubClassMap(Map<String, Class<? extends T>> subClassMap) {
        for (Entry<String, Class<? extends T>> subClass : subClassMap.entrySet()) {
            addSubClass(subClass.getKey(), subClass.getValue());
        }
    }

    /**
     * Return list of subclasses.
     * @return List of subclasses
     */
    public Map<String, Class<? extends T>> getSubClasses() {
        return subClasses;
    }
    
    public Class<? extends T> getSubClass(String discriminatorValue) {
        return subClasses.get(discriminatorValue);
    }

    /** 
     * Returns the discriminator's column name.
     * @return The discriminator column's column name 
     */
    public String getDiscriminatorColumnName() {
        String returnValue = null;
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            if (columnDefinition.isDiscriminatorColumn()) {
                returnValue = columnDefinition.getColumnName();
            }
        }
        return returnValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return persistentClass.getName();
    }

}
