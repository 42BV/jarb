package org.jarb.populator.excel.mapping.importer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores the value of a JoinTable (associative table).
 * @author Sander Benschop
 * @author Willem Eppen
 *
 */
public final class StoreJoinTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreJoinTable.class);

    /** Private constructor. */
    private StoreJoinTable() {
    }

    /** Identification number of row in Excel file. */
    private static final String IDCOLUMNNAME = "id";

    /**
     * Stores a JoinTable in ExcelRow.
     * @param sheet Representation of excel file
     * @param classDefinition ClassDefinition used to determine columnPosition
     * @param columnDefinition ColumnDefinition is the superclass of Column, JoinColumn and JoinTable.
     * @param rowPosition Vertical position number of the excelRecord
     * @param excelRow ExcelRow to save to.
     */
    public static void storeValue(Workbook excel, EntityDefinition<?> classDefinition, PropertyDefinition columnDefinition, Integer rowPosition, ExcelRow excelRow) {
        Sheet mainSheet = excel.getSheet(classDefinition.getTableName());
        Double code = (Double) mainSheet.getValueAt(rowPosition, IDCOLUMNNAME);
        Sheet joinSheet = excel.getSheet(columnDefinition.getJoinTableName());

        if (joinSheet != null) {
            Set<Integer> foreignKeyList = createForeignKeyList(joinSheet, columnDefinition, code);
            Key keyList = createJoinTableKey(columnDefinition, foreignKeyList);
            excelRow.addValue(columnDefinition, keyList);
        }
    }

    /**
     * Creates a new instance of JoinTableKey and puts the foreign keys in it.
     * @param joinTable ColumnDefinition of the JoinTable type
     * @param foreignKeyList List of foreign keys
     * @return JoinTable key instance
     */
    private static Key createJoinTableKey(PropertyDefinition joinTable, Set<Integer> foreignKeyList) {
        Key keyList = new JoinTableKey();
        keyList.setKeyValue(foreignKeyList);
        Type[] types = ((ParameterizedType) joinTable.getField().getGenericType()).getActualTypeArguments();
        keyList.setForeignClass((Class<?>) types[0]);
        return keyList;
    }

    /**
     * Creates a list of foreign keys.
     * @param excel Excel file 
     * @param joinTable ColumnDefinition of JoinTable type
     * @param code Id number
     * @return Set of foreign keys
     */
    private static Set<Integer> createForeignKeyList(Sheet sheet, PropertyDefinition joinTable, Double code) {
        Set<Integer> foreignKeyList = new HashSet<Integer>();
        for (Integer newRowPosition = 1; newRowPosition <= sheet.getLastRowNumber(); newRowPosition++) {
            Object joinColumnValue = sheet.getValueAt(newRowPosition, joinTable.getJoinColumnName());
            if(joinColumnValue instanceof Double && joinColumnValue.equals(code)) {
                Object inverseJoinColumnValue = sheet.getValueAt(newRowPosition, joinTable.getInverseJoinColumnName());
                if(inverseJoinColumnValue instanceof Number) {
                    foreignKeyList.add(((Number) inverseJoinColumnValue).intValue());
                } else {
                    LOGGER.warn("Could not convert {} foreign key value to number.", inverseJoinColumnValue);
                }
            }
        }
        return foreignKeyList;
    }

}
