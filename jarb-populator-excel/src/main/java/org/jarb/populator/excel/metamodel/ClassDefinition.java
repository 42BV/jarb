package org.jarb.populator.excel.metamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.mad.hactar.common.ReflectionUtil;

/**
 * ClassDefiniton contains all the tabledata which is bound to a certain persistent class from the domain package.
 * @author Willem Eppen
 * @author Sander Benschop
 * 
 */
public class ClassDefinition {
    /** The persistent class from the domain package belonging to a classDefinition. */
    private final Class<?> persistentClass;

    /** A set with subclasses with their discriminator values as keys. Needed for making ExcelRecords. */
    private Map<String, Class<?>> subClasses = new HashMap<String, Class<?>>();

    /** The name of the Excelsheet. */
    private String tableName;

    /** A set of ColumnDefinitions. */
    private List<PropertyDefinition> columnDefinitions = new ArrayList<PropertyDefinition>();

    /** An instance of the WorksheetDefinition belonging to a classDefinition. */
    private WorksheetDefinition worksheetDefinition; // TODO: Not sure what the purpose of this is

    public ClassDefinition(Class<?> persistentClass) {
        this.persistentClass = persistentClass;
    }

    /**
     * Creates a new instance of a persistentClass.
     * @return persistentClass instance of persistent class.
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    public Object createInstance() throws InstantiationException, IllegalAccessException {
        return ReflectionUtil.instantiate(persistentClass);
    }

    /**
     * Returns the persistentClass belonging to classDefinition.
     * @return persistentClass instance from domain package
     */
    public Class<?> getPersistentClass() {
        return persistentClass;
    }

    /**
     * Adds a columnDefinition to the classDefinition.
     * @param columnDefinition instance of ColumnDefinition
     */
    public void addColumnDefinition(final PropertyDefinition columnDefinition) {
        this.columnDefinitions.add(columnDefinition);
    }

    /**
     * Adds a whole list of columnDefinitions at once.
     * @param columnDefinitionList instances of ColumnDefinition
     */
    public void addColumnDefinitionList(final List<PropertyDefinition> columnDefinitionList) {
        for (PropertyDefinition columnDefinition : columnDefinitionList) {
            this.columnDefinitions.add(columnDefinition);
        }
    }

    /**
     * Returns all the columnDefinitions belonging to the classDefinition.
     * @return set of ColumnDefinitions
     */
    public List<PropertyDefinition> getColumnDefinitions() {
        return this.columnDefinitions;
    }

    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<String>();
        for (PropertyDefinition columnDefinition : columnDefinitions) {
            columnNames.add(columnDefinition.getColumnName());
        }
        return columnNames;
    }

    /**
     * Returns a ColumnDefinition that holds the passed fieldname.
     * @param fieldName Fieldname to search the ColumnDefinitions for
     * @return ColumnDefinition
     */
    public PropertyDefinition getColumnDefinitionByFieldName(String fieldName) {
        PropertyDefinition returnColumnDef = null;
        for (PropertyDefinition columnDefinition : columnDefinitions) {
            if (columnDefinition.getFieldName().equals(fieldName)) {
                returnColumnDef = columnDefinition;
            }
        }
        return returnColumnDef;
    }

    /**
     * Returns a ColumnDefinition that holds the passed column name.
     * @param columnName Column name to search the ColumnsDefinitions for
     * @return ColumnDefinition
     */
    public PropertyDefinition getColumnDefinitionByColumnName(String columnName) {
        PropertyDefinition returnColumnDef = null;
        for (PropertyDefinition columnDefinition : columnDefinitions) {
            if (columnDefinition.getColumnName().equals(columnName)) {
                returnColumnDef = columnDefinition;
            }
        }
        return returnColumnDef;
    }

    /**
     * Returns the tableName of the classDefinition.
     * @return tableName String
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * Sets the tableName of the classDefinition.
     * @param tableName String
     */
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    /**
     * Sets the worksheetDefinition belonging to the classDefinition.
     * @param worksheetDefinition instance
     */
    public void setWorksheetDefinition(final WorksheetDefinition worksheetDefinition) {
        this.worksheetDefinition = worksheetDefinition;
    }

    /**
     * Returns the worksheetDefinition belonging to the classDefinition.
     * @return worksheetDefinition instance
     */
    public WorksheetDefinition getWorksheetDefinition() {
        return this.worksheetDefinition;
    }

    /**
     * Adds a persistent subclass to the ClassDefinition, mapped by its discriminator value. Needed for making proper Excelrecords.
     * @param discriminatorValue Discriminator value of subclass
     * @param persistentSubClass Persistent class of subclass
     */
    public void addSubClass(String discriminatorValue, Class<?> persistentSubClass) {
        this.subClasses.put(discriminatorValue, persistentSubClass);
    }

    /**
     * Adds the whole subclass map.
     * @param subClassMap Map of subclasses to be added
     */
    public void addSubClassMap(Map<String, Class<?>> subClassMap) {
        for (Entry<String, Class<?>> subClass : subClassMap.entrySet()) {
            this.addSubClass(subClass.getKey(), subClass.getValue());
        }
    }

    /**
     * Return list of subclasses.
     * @return List of subclasses
     */
    public Map<String, Class<?>> getSubClasses() {
        return subClasses;
    }

    /** 
     * Returns the discriminator's column name.
     * @return The discriminator column's column name 
     */
    public String getDiscriminatorColumnName() {
        String returnValue = null;
        for (PropertyDefinition columnDefinition : columnDefinitions) {
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
