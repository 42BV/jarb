package org.jarb.populator.excel.mapping.importer;

import org.jarb.populator.excel.mapping.CouldNotConvertException;
import org.jarb.populator.excel.mapping.ValueConversionService;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.utils.BeanPropertyUtils;
import org.jarb.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores the value of a regular Excel column.
 * @author Sander Benschop
 * @author Willem Eppen
 *
 */
public final class StoreColumn {
    private static final Logger logger = LoggerFactory.getLogger(StoreColumn.class);

    /** Private constructor. */
    private StoreColumn() {
    }

    /**
     * Stores a Column in ExcelRow.
     * @param excel Representation of excel file
     * @param classDefinition ClassDefinition used to determine columnPosition
     * @param columnDefinition ColumnDefinition is the superclass of Column, JoinColumn and JoinTable.
     * @param rowPosition Vertical position number of the excelRecord
     * @param excelRow ExcelRow to save to.
     * @throws NoSuchFieldException Thrown when a field is not available
     */
    public static void storeValue(Workbook excel, EntityDefinition<?> classDefinition, PropertyDefinition columnDefinition, Integer rowPosition, ExcelRow excelRow)
            throws NoSuchFieldException {
        WorksheetDefinition worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        Integer columnPosition = worksheetDefinition.getColumnPosition(columnDefinition.getColumnName());

        Sheet sheet = excel.getSheet(classDefinition.getTableName());
        Object cellValue = getCellValue(sheet, rowPosition, columnPosition);
        logger.debug("field: " + columnDefinition.getName() + " column: " + columnDefinition.getColumnName() + " value:[" + cellValue + "]");

        if (BeanPropertyUtils.hasProperty(excelRow.getCreatedInstance(), columnDefinition.getName())) {
            setExcelRowFieldValue(excelRow.getCreatedInstance(), columnDefinition.getName(), cellValue);
        } else if (columnDefinition.isEmbeddedAttribute()) {
            Object embeddedField = columnDefinition.getEmbeddablePath().traverse(excelRow.getCreatedInstance());
            setExcelRowFieldValue(embeddedField, columnDefinition.getName(), cellValue);
        }
    }

    /**
     * Sets an Excelrow's field value via reflection. Works for regular fields, embedded fields and Enumerations.
     * @param excelRow Excelrow to add the field value to
     * @param fieldName Name of field which value is to be set
     * @param cellValue Value of the field that is to be saved
     */
    private static void setExcelRowFieldValue(Object excelRow, String fieldName, Object cellValue) {
        final Class<?> fieldType = ReflectionUtils.getFieldType(excelRow, fieldName);
        try {
            Object fieldValue = new ValueConversionService().convert(cellValue, fieldType);
            ReflectionUtils.setFieldValue(excelRow, fieldName, fieldValue);
        } catch (CouldNotConvertException e) {
            logger.warn("Could not convert '{}' into a {}, thus '{}' will remain unchanged.", new Object[] { cellValue, fieldType, fieldName });
        }
    }

    /**
     * Returns the cellValue from the excel file.
     * @param excel Excel file to get cellValue from
     * @param rowPosition Rowposition of the cell
     * @param columnPosition ColumnPosition of the cell
     * @return CellValue from the specified location
     */
    private static Object getCellValue(Sheet sheet, Integer rowPosition, Integer columnPosition) {
        Object cellValue = null;
        if (columnPosition != null) {
            cellValue = sheet.getValueAt(rowPosition, columnPosition);
        }
        return cellValue;
    }
}
