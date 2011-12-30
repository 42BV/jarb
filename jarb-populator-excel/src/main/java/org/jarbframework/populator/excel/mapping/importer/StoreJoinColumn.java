package org.jarbframework.populator.excel.mapping.importer;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.util.JpaUtils;
import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores the value of a JoinColumn.
 * @author Sander Benschop
 * @author Willem Eppen
 *
 */
public final class StoreJoinColumn {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreJoinColumn.class);

    /** Private constructor. */
    private StoreJoinColumn() {
    }

    /**
     * Stores a JoinColumn in ExcelRow.
     * @param excel Representation of excel file
     * @param definition ClassDefinition used to determine columnPosition
     * @param columnDefinition ColumnDefinition is the superclass of Column, JoinColumn and JoinTable.
     * @param rowPosition Vertical position number of the excelRecord
     * @param excelRow ExcelRow to save to.
     */
    public static void storeValue(Workbook excel, Definition definition, PropertyDefinition columnDefinition, Integer rowPosition, ExcelRow excelRow) {
        Sheet sheet = excel.getSheet(JpaUtils.getTableNameOfDefinition(definition));
        Object cellValue = sheet.getValueAt(rowPosition, columnDefinition.getColumnName());
        LOGGER.debug("field: " + columnDefinition.getName() + " column: " + columnDefinition.getColumnName() + " value:[" + cellValue + "]");
        if (cellValue != null) {
            // Sets the Key
            Key joinColumnKey = createJoinColumnKey(columnDefinition, cellValue);
            excelRow.addValue(columnDefinition, joinColumnKey);
        }
    }
    
    /**
     * Creates a JoinColumnKey. If the key is of a numeric type it will be cast to Integer to prevent Excel formatting issues.
     * @param propertyDefinition PropertyDefinition used to get the foreign class from
     * @param cellValue Key value from Excel file
     * @return JoinColumnKey
     */
    private static Key createJoinColumnKey(PropertyDefinition propertyDefinition, Object cellValue) {
        if (cellValue instanceof Number) {
            cellValue = ((Number) cellValue).intValue();
        }
        Key joinColumnKey = new JoinColumnKey();
        joinColumnKey.setKeyValue(cellValue);
        joinColumnKey.setForeignClass(propertyDefinition.getField().getType());
        return joinColumnKey;
    }
    
    
}
