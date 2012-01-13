package org.jarbframework.populator.excel.mapping.importer;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StoreInversedJoinColumn {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreInversedJoinColumn.class);

    /** Utility class, do not instantiate. */
    private StoreInversedJoinColumn() {
    }

    /**
     * Stores an InversedJoinColumn in ExcelRow.
     * @param excel Representation of excel file
     * @param classDefinition ClassDefinition used to determine columnPosition
     * @param columnDefinition ColumnDefinition is the superclass of Column, JoinColumn and JoinTable.
     * @param rowPosition Vertical position number of the excelRecord
     * @param excelRow ExcelRow to save to.
     */
    public static void storeValue(Workbook excel, Definition classDefinition, PropertyDefinition columnDefinition, Integer rowPosition, ExcelRow excelRow) {
        
    }

}
