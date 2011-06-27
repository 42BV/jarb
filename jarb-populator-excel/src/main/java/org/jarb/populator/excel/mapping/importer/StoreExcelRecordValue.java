package org.jarb.populator.excel.mapping.importer;

import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Function which is responsible for saving values to ExcelRecords, put here because the ExcelRow class was getting quite large.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
public final class StoreExcelRecordValue {

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
    public static void storeValue(Workbook excel, EntityDefinition<?> classDefinition, PropertyDefinition columnDefinition, 
            Integer rowPosition, ExcelRow excelRow) throws NoSuchFieldException {
        switch(columnDefinition.getDatabaseType()) {
        case COLUMN:
            StoreColumn.storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
            break;
        case JOIN_TABLE:
            StoreJoinTable.storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
            break;
        case JOIN_COLUMN:
            StoreJoinColumn.storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
            break;
        }
    }
    
}
