package org.jarbframework.populator.excel.mapping.importer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.InverseJoinColumnReferenceProperties;
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
    public static void storeValue(Workbook excel, EntityDefinition<?> classDefinition, PropertyDefinition propertyDefinition, Integer rowPosition,
            ExcelRow excelRow) {
        InverseJoinColumnReferenceProperties inverseJoinColumnReferenceProperties = propertyDefinition.getInverseJoinColumnReferenceProperties();

        Key inverseJoinColumnKey = new InverseJoinColumnKey();
        inverseJoinColumnKey.setForeignClass(inverseJoinColumnReferenceProperties.getEmbeddableType().getClass());

        Sheet sheet = excel.getSheet(classDefinition.getTableName());
        Row row = sheet.getRowAt(rowPosition);

        Map<String, Object> joinColumnKeyMap = new HashMap<String, Object>();

        for (Entry<String, String> joinColumnEntry : inverseJoinColumnReferenceProperties.getReferencedColumnAndJoinColumnNamesHashMap().entrySet()) {
            String referencedColumnName = joinColumnEntry.getKey();
            String joinColumnName = joinColumnEntry.getValue();
            Object identifier = row.getCellAt(referencedColumnName);
            //Cell cell = row.getCellAt(joinColumnName);
            //joinColumnKeyMap.put(joinColumnName, cell.getValue());
        }

        System.out.println("");
    }

}
