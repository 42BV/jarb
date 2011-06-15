package org.jarb.populator.excel.mapping.excelrow;

import java.util.HashMap;
import java.util.Map;

import org.jarb.populator.excel.metamodel.ColumnDefinition;

/**
 * Object which represents an Excel row. Can add data to valueMap and retrieve it.
 * @author Willem Eppen
 * @author Sander Benschop
 */
public class ExcelRow {

    /** Instance of a peristable class. */
    private Object createdInstance;

    /** ValueMap with columnDefinitions and their keys. */
    private Map<ColumnDefinition, Key> valueMap;

    /**
     * Public constructor for ExcelRow. Creates the valueMap and sets this.createdInstance to passed ClassDefinition.
     * @param entity Instance of a persistable class
     */
    public ExcelRow(final Object entity) {
        this.createdInstance = entity;
        valueMap = new HashMap<ColumnDefinition, Key>();
    }

    /**
     * Adds a ColumnDefinition to the valueMap.
     * @param columnDefinition Instance of ColumnDefinition
     * @param value Key for ColumnDefinition
     */
    public void addValue(ColumnDefinition columnDefinition, Key value) {
        valueMap.put(columnDefinition, value);
    }

    /**
     * Returns a hashmap with columndefinitions and keys.
     * @return hashmap with columnDefinitions and their corresponding keys
     */
    public Map<ColumnDefinition, Key> getValueMap() {
        return this.valueMap;
    }

    /**
     * Returns instance of ClassDefinition as passed to ExcelRow through the constructor.
     * @return Instance of ClassDefinition
     */
    public Object getCreatedInstance() {
        return this.createdInstance;
    }
}
