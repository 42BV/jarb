package org.jarbframework.populator.excel.mapping.importer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.InverseJoinColumnReferenceProperties;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.workbook.Cell;
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
        inverseJoinColumnKey.setForeignClass(inverseJoinColumnReferenceProperties.getEmbeddableType().getJavaType());

        Sheet sheet = excel.getSheet(classDefinition.getTableName());
        Row row = sheet.getRowAt(rowPosition);

        Map<String, Object> joinColumnKeyMap = new HashMap<String, Object>();

        for (Entry<String, String> joinColumnEntry : inverseJoinColumnReferenceProperties.getReferencedColumnAndJoinColumnNamesHashMap().entrySet()) {
            String referencedColumnName = joinColumnEntry.getKey();
            Cell cell = row.getCellAt(referencedColumnName);
            if (cell != null) {
                Integer intValue = convertCellValueToNumericValue(cell);
                joinColumnKeyMap.put(referencedColumnName, intValue);
            }
        }

        inverseJoinColumnKey.setKeyValue(joinColumnKeyMap);
        excelRow.addValue(propertyDefinition, inverseJoinColumnKey);
    }

    /**
     * Converts a cell value to an Integer value if possible.
     * @param cell Cell to retrieve cellvalue from
     @ @return Integer value
     */
    private static Integer convertCellValueToNumericValue(Cell cell) {
        Integer intValue = null;
        Object cellValue = cell.getValue();
        if (cellValue instanceof Number) {
            intValue = ((Number) cellValue).intValue();
        } else {
            LOGGER.warn("Could not convert {} foreign key value to number.", cellValue);
        }
        return intValue;
    }
}
