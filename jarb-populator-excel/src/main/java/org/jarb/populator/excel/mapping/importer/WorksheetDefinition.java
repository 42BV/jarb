package org.jarb.populator.excel.mapping.importer;

import java.util.HashMap;
import java.util.Map;

import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analyses Excel worksheets, and adds columnPositions to it.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
public class WorksheetDefinition {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorksheetDefinition.class);

    /** Hashmap containing columnPositions and their names. */
    private Map<String, Integer> columnPositions = new HashMap<String, Integer>();

    /**
     * Analyses a worksheet, adding its column names and positions from the excel file to the worksheet.
     * @param classDefinition ClassDefinition to retrieve the tableName from, which is used to set the Excel sheet
     * @param excel The Excel file to read from
     * @return WorksheetDefinition with column names and positions
     */
    public static WorksheetDefinition analyzeWorksheet(final EntityDefinition<?> classDefinition, final Workbook excel) {
        WorksheetDefinition worksheetDefinition = new WorksheetDefinition();
        LOGGER.debug("Analyzing worksheet: [" + classDefinition.getTableName() + "]");
        Sheet sheet = excel.getSheet(classDefinition.getTableName());

        for (PropertyDefinition columnDefinition : classDefinition.properties()) {
            final String columnName = columnDefinition.getColumnName();
            LOGGER.debug("  field name: [" + columnDefinition.getName() + "], column name: [" + columnName + "]");
            if (columnDefinition.hasColumn()) {
                if(sheet.containsColumn(columnName)) {
                    worksheetDefinition.addColumnPosition(columnName, classDefinition.getTableName(), sheet.indexOfColumn(columnName));
                } else {
                    LOGGER.warn("Column name " + columnDefinition.getColumnName() + " was not present in the Worksheet.");
                }
            }
        }
        
        if(classDefinition.hasDiscriminatorColumn()) {
            final String discriminatorColumnName = classDefinition.getDiscriminatorColumnName();
            LOGGER.debug("  discriminator column name: [" + discriminatorColumnName + "]");
            if(sheet.containsColumn(discriminatorColumnName)) {
                worksheetDefinition.addColumnPosition(discriminatorColumnName, classDefinition.getTableName(), sheet.indexOfColumn(discriminatorColumnName));
            } else {
                LOGGER.warn("Discriminator column {} is missing in the worksheet.", discriminatorColumnName);
            }
        }
        
        return worksheetDefinition;
    }

    /**
     * Adds a columnPosition to the worksheetDefinition.
     * @param columnName ColumnName from Excel sheet
     * @param worksheetName Name of the current worksheet from the Excel project
     * @param columnPosition Column number, 1 based
     */
    public void addColumnPosition(final String columnName, final String worksheetName, final Integer columnPosition) {
        if (columnPosition != null && columnPosition >= 0) {
            columnPositions.put(columnName, columnPosition);
        } else {
            LOGGER.info("Could not find column \"" + columnName + "\" in worksheet \"" + worksheetName + "\"");
        }
    }

    /**
     * Returns a ColumnPosition for specified FieldName.
     * @param columnName ColumnName of 
     * @return ColumnPosition of FieldName
     */
    public Integer getColumnPosition(String columnName) {
        return columnPositions.get(columnName);
    }
}
