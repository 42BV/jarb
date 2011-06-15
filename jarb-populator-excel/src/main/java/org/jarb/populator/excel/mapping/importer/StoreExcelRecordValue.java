package org.jarb.populator.excel.mapping.importer;

import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.Column;
import org.jarb.populator.excel.metamodel.JoinColumn;
import org.jarb.populator.excel.metamodel.JoinTable;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.jarb.populator.excel.workbook.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Function which is responsible for saving values to ExcelRecords, put here because the ExcelRow class was getting quite large.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
public final class StoreExcelRecordValue {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreExcelRecordValue.class);

    /** Private constructor. */
    private StoreExcelRecordValue() {
    }

    /**
     * Calls functions to save data. There are three possible types: Column, JoinColumn and JoinTable. 
     * Each have their own saving rules, for each one the appropriate function is called.
     * @param excel Representation of excel file
     * @param classDefinition ClassDefinition used to determine columnPosition
     * @param columnDefinition ColumnDefinition is the superclass of Column, JoinColumn and JoinTable.
     * @param rowPosition Vertical position number of the excelRecord
     * @param excelRow ExcelRow to save to.
     * @throws NoSuchFieldException Thrown when a field is not available
     */
    public static void storeValue(Workbook excel, ClassDefinition<?> classDefinition, ColumnDefinition columnDefinition, //
            Integer rowPosition, ExcelRow excelRow) throws NoSuchFieldException {
        if (columnDefinition instanceof Column) {
            StoreColumn.storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
        } else if (columnDefinition instanceof JoinTable) {
            StoreJoinTable.storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
        } else if (columnDefinition instanceof JoinColumn) {
            StoreJoinColumn.storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
        } else {
            LOGGER.warn("ColumnDefinition is not an instance of Column, JoinTable or JoinColumn. Cannot store data.");
        }
    }
}
