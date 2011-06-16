package org.jarb.populator.excel.workbook.generator;

import java.util.Set;

import javax.persistence.PersistenceUnitUtil;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.util.ReflectionUtils;
import org.jarb.populator.excel.workbook.validator.FieldValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles all associative columnDefinitions to create new Excelsheets with associative table data from the database in it.
 * @author Sander Benschop
 *
 */
public final class AssociativeTableGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssociativeTableGenerator.class);

    /** Private constructor. */
    private AssociativeTableGenerator() {
    }

    /**
     * Creates associative tables that are annotated as @JoinTable under the current databaseRecord.
     * @param workbook Excel workbook that contains all the sheets
     * @param puUtil PersistenceUnitUtil is needed to extract ID's
     * @param associativeColumnDefinitions Set of ColumnDefinitions that are @JoinTable's
     * @param databaseRecord Current databaserecord that is being handled
     * @throws NoSuchFieldException 
     */
    protected static void createAssociativeTables(HSSFWorkbook workbook, PersistenceUnitUtil puUtil, Set<PropertyDefinition> associativeColumnDefinitions,
            Object databaseRecord) throws NoSuchFieldException {
        //Process the associative tables
        for (PropertyDefinition columnDefinition : associativeColumnDefinitions) {
            createAssociateTableSheet(workbook, puUtil, databaseRecord, columnDefinition);
        }
    }

    /**
     * Creates a new sheet for an associative table.
     * @param workbook Excel workbook that contains all the sheets
     * @param puUtil PersistenceUnitUtil is needed to extract ID's
     * @param databaseRecord Current databaserecord that is being handled
     * @param columnDefinition Representation of a database column
     * @throws NoSuchFieldException 
     */
    private static void createAssociateTableSheet(HSSFWorkbook workbook, PersistenceUnitUtil puUtil, Object databaseRecord, PropertyDefinition columnDefinition)
            throws NoSuchFieldException {
        //The columnName is the name of the associative table. Get the proper sheet.
        HSSFSheet sheet = workbook.getSheet(columnDefinition.getJoinTableName());
        int rowNumber = sheet.getLastRowNum() + 1;

        Set<?> collectionSet = getCollectionSet(databaseRecord, columnDefinition);

        HSSFRow row;
        if (collectionSet != null) {
            for (Object collectionItem : collectionSet) {
                row = sheet.createRow(rowNumber);
                createAssociativeCollectionRow(puUtil, databaseRecord, sheet, row, columnDefinition, collectionItem);
                rowNumber++;
            }
        }
    }

    /**
     * Returns a collection set with the fields belonging to the JoinTable columnDefinition.
     * @param databaseRecord DatabaseRecord to get the field from
     * @param joinTable JoinTable columnDefinition
     * @return CollectionSet
     * @throws NoSuchFieldException 
     */
    private static Set<?> getCollectionSet(Object databaseRecord, PropertyDefinition joinTable) throws NoSuchFieldException {
        Set<?> collectionSet = null;
        if (FieldValidator.isExistingField(joinTable.getName(), databaseRecord.getClass())) {
            Object collectionObject = ReflectionUtils.getFieldValue(databaseRecord, joinTable.getName());
            collectionSet = (Set<?>) collectionObject;
        }
        return collectionSet;
    }

    /**
     * Creates a single row for an associative collection.
     * @param puUtil PersistenceUnitUtil is needed to extract ID's
     * @param databaseRecord Current databaserecord that is being handled
     * @param sheet The current Excel sheet
     * @param row The current Excel row
     * @param dateFormatStyle Specified date format, which is yyyy-mm-dd (database conventional)
     * @param joinTable Associative table type of ColumnDefinition
     * @param collectionItem One associative row object 
     */
    private static void createAssociativeCollectionRow(PersistenceUnitUtil puUtil, Object databaseRecord, HSSFSheet sheet, HSSFRow row, PropertyDefinition joinTable,
            Object collectionItem) {
        for (int columnNumber = 0; columnNumber < sheet.getRow(0).getPhysicalNumberOfCells(); columnNumber++) {
            createAssociativeExcelColumn(puUtil, databaseRecord, sheet, row, joinTable, collectionItem, columnNumber);
        }
    }

    /**
     * Creates an associative Excel column. Can either be on the owning or on the other side of the relation.
     * Associative tables are mapped as @JoinTable.
     * @param puUtil PersistenceUnitUtil is needed to extract ID's
     * @param databaseRecord Current databaserecord that is being handled
     * @param sheet The current Excel sheet
     * @param row The current Excel row
     * @param dateFormatStyle Specified date format, which is yyyy-mm-dd (database conventional)
     * @param joinTable Associative table type of ColumnDefinition
     * @param collectionItem One associative row object 
     * @param columnNumber The horizontal position of the Excel column (0-based)
     */
    private static void createAssociativeExcelColumn(PersistenceUnitUtil puUtil, Object databaseRecord, HSSFSheet sheet, HSSFRow row, PropertyDefinition joinTable,
            Object collectionItem, int columnNumber) {
        String columnName = getColumnNameFromSheet(sheet, columnNumber);
        CellStyle dateFormatStyle = DateFormatStyle.getDateFormatStyle(sheet.getWorkbook());

        if (columnName.equals(joinTable.getJoinColumnName())) {
            createAssociativeIdCell(puUtil, databaseRecord, row, dateFormatStyle, columnNumber);
        } else if (columnName.equals(joinTable.getInverseJoinColumnName())) {
            createAssociativeIdCell(puUtil, collectionItem, row, dateFormatStyle, columnNumber);
        } else {
            LOGGER.warn("Columnname [" + columnName + "] is neither the JoinColumnName or InverseJoinColumnName.");
        }
    }

    /**
     * Returns the columnName on the specified horizontal position of the sheet.
     * @param sheet Excel sheet to search the column in
     * @param columnNumber Horizontal position (0-based)
     * @return Column name
     */
    private static String getColumnNameFromSheet(HSSFSheet sheet, int columnNumber) {
        return sheet.getRow(0).getCell(columnNumber).getStringCellValue();
    }

    /**
     * Creates an associative cell with an ID in it belonging to either the owning or the other class.
     * @param puUtil PersistenceUnitUtil is needed to extract ID's
     * @param databaseRecord Current databaserecord that is being handled
     * @param row The current Excel row
     * @param dateFormatStyle Specified date format, which is yyyy-mm-dd (database conventional)
     * @param columnNumber The horizontal position of the Excel column (0-based)
     */
    private static void createAssociativeIdCell(PersistenceUnitUtil puUtil, Object databaseRecord, HSSFRow row, CellStyle dateFormatStyle, int columnNumber) {
        Object associativeIdObject = puUtil.getIdentifier(databaseRecord);
        CellValueSetter.setCellValueByProperType(row, columnNumber, associativeIdObject, dateFormatStyle);
    }

}
