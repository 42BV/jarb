package org.jarb.populator.excel.mapping.importer;

import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.mapping.excelrow.JoinColumnKey;
import org.jarb.populator.excel.mapping.excelrow.Key;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.JoinColumn;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Stores the value of a JoinColumn.
 * @author Sander Benschop
 * @author Willem Eppen
 *
 */
public final class StoreJoinColumn {

    /** Private constructor. */
    private StoreJoinColumn() {
    }

    /**
     * Stores a JoinColumn in ExcelRow.
     * @param excel Representation of excel file
     * @param classDefinition ClassDefinition used to determine columnPosition
     * @param columnDefinition ColumnDefinition is the superclass of Column, JoinColumn and JoinTable.
     * @param rowPosition Vertical position number of the excelRecord
     * @param excelRow ExcelRow to save to.
     */
    public static void storeValue(Workbook excel, ClassDefinition classDefinition, PropertyDefinition columnDefinition, //
            Integer rowPosition, ExcelRow excelRow) {
        if (columnDefinition instanceof JoinColumn) {
            JoinColumn joinColumn = (JoinColumn) columnDefinition;
            Sheet sheet = excel.getSheet(classDefinition.getTableName());
            Double cellValue = (Double) sheet.getCellValueAt(rowPosition, joinColumn.getColumnName());
            System.out.println("field: " + joinColumn.getFieldName() + " column: " + joinColumn.getColumnName() + " value:[" + cellValue + "]");
            if (cellValue != null) {
                // Sets the Key
                Key keyValue = new JoinColumnKey();
                keyValue.setKeyValue(cellValue.intValue());
                keyValue.setForeignClass(joinColumn.getField().getType());
                excelRow.addValue(joinColumn, keyValue);
            }
        }
    }
}
