package org.jarbframework.populator.excel.mapping.importer;

import java.lang.reflect.Field;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.workbook.Row;
import org.jarbframework.populator.excel.workbook.Sheet;
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
     * @param propertyDefinition ColumnDefinition is the superclass of Column, JoinColumn and JoinTable.
     * @param rowPosition Vertical position number of the excelRecord
     * @param excelRow ExcelRow to save to.
     */
    public static void storeValue(Workbook excel, Definition classDefinition, PropertyDefinition propertyDefinition, Integer rowPosition, ExcelRow excelRow) {
        String inversedReferenceSheetName = propertyDefinition.getInverseJoinColumnReferenceProperties().getReferencedTableName();
        Sheet inversedReferenceSheet = excel.getSheet(inversedReferenceSheetName);

        getCollectionTypeClassForPropertyDefinition(propertyDefinition);
        for (Row row : inversedReferenceSheet.getRows()) {
            Key inverseJoinColumnKey = new InverseJoinColumnKey();
            System.out.println("");
        }
    }

    private static Class<?> getCollectionTypeClassForPropertyDefinition(PropertyDefinition propertyDefinition) {
        Field field = propertyDefinition.getField();
        System.out.println("");
        return null;
    }

}
