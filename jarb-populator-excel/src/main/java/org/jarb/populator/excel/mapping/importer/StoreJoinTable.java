package org.jarb.populator.excel.mapping.importer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.mapping.excelrow.JoinTableKey;
import org.jarb.populator.excel.mapping.excelrow.Key;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.JoinTable;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Stores the value of a JoinTable (associative table).
 * @author Sander Benschop
 * @author Willem Eppen
 *
 */
public final class StoreJoinTable {

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
    public static void storeValue(Workbook excel, ClassDefinition<?> classDefinition, ColumnDefinition columnDefinition, //
            Integer rowPosition, ExcelRow excelRow) {

        if (columnDefinition instanceof JoinTable) {
            JoinTable joinTable = (JoinTable) columnDefinition;

            Sheet mainSheet = excel.getSheet(classDefinition.getTableName());
            Double code = (Double) mainSheet.getCellValueAt(rowPosition, mainSheet.indexOfColumn(IDCOLUMNNAME));
            Sheet joinSheet = excel.getSheet(joinTable.getColumnName());

            if (joinSheet != null) {
                Set<Integer> foreignKeyList = createForeignKeyList(joinSheet, joinTable, code);
                Key keyList = createJoinTableKey(joinTable, foreignKeyList);
                excelRow.addValue(joinTable, keyList);
            }
        }
    }

    /**
     * Creates a new instance of JoinTableKey and puts the foreign keys in it.
     * @param joinTable ColumnDefinition of the JoinTable type
     * @param foreignKeyList List of foreign keys
     * @return JoinTable key instance
     */
    private static Key createJoinTableKey(JoinTable joinTable, Set<Integer> foreignKeyList) {
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
    private static Set<Integer> createForeignKeyList(Sheet sheet, JoinTable joinTable, Double code) {
        Set<Integer> foreignKeyList = new HashSet<Integer>();
        for (Integer newRowPosition = 1; newRowPosition <= sheet.getLastRowNumber(); newRowPosition++) {
            if (((Double) sheet.getCellValueAt(newRowPosition, joinTable.getJoinColumnName())).equals(code)) {
                Integer foreignKey = ((Double) sheet.getCellValueAt(newRowPosition, joinTable.getInverseJoinColumnName())).intValue();
                foreignKeyList.add(foreignKey);
            }
        }
        return foreignKeyList;
    }

}
