package org.jarb.populator.excel.mapping.importer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.mapping.excelrow.ForeignRelationsMapper;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses through Excel data. Can create a set of columnDefinitions and add to a columnDefinition.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
public final class ExcelImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelImporter.class);

    /** Private constructor. */
    private ExcelImporter() {
    }

    /** Identification number of row in Excel file. */
    private static final String IDCOLUMNNAME = "id";

    /**
     * Returns an objectModel hashmap containing ClassDefinitions with their corresponding Excel records, also by calling parseWorksheet listed below.
     * @param excel Excel file in use to be stored in the objectModel
     * @param classDefinitions All classDefinitions to be stored in the objectModel and saved to the database later on
     * @return Hashmap containing all classdefinitions and parsed excel records.
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws NoSuchFieldException Thrown when a field is not available
     */
    public static Map<ClassDefinition<?>, Map<Integer, ExcelRow>> parseExcel(Workbook excel, List<ClassDefinition<?>> classDefinitions)
            throws InstantiationException, IllegalAccessException, NoSuchFieldException {
        Map<ClassDefinition<?>, Map<Integer, ExcelRow>> objectModel = new HashMap<ClassDefinition<?>, Map<Integer, ExcelRow>>();

        for (ClassDefinition<?> classDefinition : classDefinitions) {
            LOGGER.debug("Importing " + classDefinition.getTableName());
            objectModel.put(classDefinition, parseWorksheet(excel, classDefinition));
        }

        // Create foreign key relations, resulting in a fully prepared instance

        for (Map.Entry<ClassDefinition<?>, Map<Integer, ExcelRow>> objectModelEntry : objectModel.entrySet()) {
            for (Map.Entry<Integer, ExcelRow> excelRowEntry : objectModelEntry.getValue().entrySet()) {
                try {
                    ForeignRelationsMapper.makeForeignRelations(excelRowEntry.getValue(), objectModel);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return objectModel;
    }

    /**
     * Returns a hashmap containing Excel records and their cell values.
     * @param excel Excel file in use to be stored in the objectModel
     * @param classDefinition ClassDefinition containing the columnDefinitions the values will be stored in
     * @return Hashmap containing Excel records and their cell values
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws NoSuchFieldException Thrown when a field is not available
     */
    public static Map<Integer, ExcelRow> parseWorksheet(final Workbook excel, final ClassDefinition<?> classDefinition) throws InstantiationException,
            IllegalAccessException, NoSuchFieldException {
        Map<Integer, ExcelRow> createdInstances = new HashMap<Integer, ExcelRow>();

        Sheet sheet = excel.getSheet(classDefinition.getTableName());
        String discriminatorColumnName = classDefinition.getDiscriminatorColumnName();
        for (Integer rowPosition = 1; rowPosition <= sheet.getLastRowNumber(); rowPosition++) {
            ExcelRow excelRow = createFittingExcelRow(sheet, classDefinition, discriminatorColumnName, rowPosition);
            storeExcelRecordByColumnDefinitions(excel, classDefinition, rowPosition, excelRow);
            putCreatedInstance(sheet, classDefinition, createdInstances, rowPosition, excelRow);
        }
        return createdInstances;
    }

    /**
     * Used to create the right type of ExcelRow, if columns from subclasses are present, the subclass must be instantiated.
     * @param sheet Excel file needed to get the Discriminator value
     * @param classDefinition ClassDefinition needed to get the Discriminator column
     * @param discriminatorColumnName The discriminator's column name
     * @param rowPosition The rowPosition in the Excel file (0 based) 
     * @return An ExcelRecord of the right type
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     */
    private static ExcelRow createFittingExcelRow(final Sheet sheet, final ClassDefinition<?> classDefinition, String discriminatorColumnName, Integer rowPosition)
            throws InstantiationException, IllegalAccessException {
        Class<?> entityClass = determineEntityClass(sheet, classDefinition, discriminatorColumnName, rowPosition);
        return new ExcelRow(ReflectionUtils.instantiate(entityClass));
    }
    
    private static Class<?> determineEntityClass(final Sheet sheet, final ClassDefinition<?> classDefinition, String discriminatorColumnName, Integer rowPosition) {
        Class<?> entityClass = classDefinition.getPersistentClass();
        if (discriminatorColumnName != null) {
            WorksheetDefinition worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, sheet.getWorkbook());
            Integer discriminatorPosition = worksheetDefinition.getColumnPosition(discriminatorColumnName);
            String discriminatorValue = getDiscriminatorValueFromExcelFile(sheet, rowPosition, discriminatorPosition);
            if (discriminatorValue != null) {
                entityClass = classDefinition.getSubClasses().get(discriminatorValue);
            }
        }
        return entityClass;
    }

    /**
     * Returns the discriminator value from the Excel sheet.
     * @param sheet Excel file to get the value from
     * @param rowPosition Row position (0-based)
     * @param discriminatorPosition Column position (0-based)
     * @return The value from the discriminator column in the Excel sheet
     */
    private static String getDiscriminatorValueFromExcelFile(final Sheet sheet, Integer rowPosition, Integer discriminatorPosition) {
        String discriminatorValue = null;
        if (discriminatorPosition != null) {
            discriminatorValue = (String) sheet.getCellValueAt(rowPosition, discriminatorPosition);
        }
        return discriminatorValue;
    }

    /**
     * Puts a new instance in the value map.
     * @param sheet Excel file the data is gathered from
     * @param classDefinition ClassDefinition representing a database table
     * @param createdInstances A map of created instances
     * @param rowPosition The rowPosition in the Excel file (0 based) 
     * @param excelRow An excelRow to store in the value map
     */
    private static void putCreatedInstance(final Sheet sheet, final ClassDefinition<?> classDefinition, Map<Integer, ExcelRow> createdInstances,
            Integer rowPosition, ExcelRow excelRow) {
        if (sheet.containsColumn(IDCOLUMNNAME)) {
            Integer idNumber = ((Double) sheet.getCellValueAt(rowPosition, IDCOLUMNNAME)).intValue();
            if (!createdInstances.containsKey(idNumber)) {
                createdInstances.put(idNumber, excelRow);
            } else {
                // Primary key is not unique
                LOGGER.error("IDCOLUMNNAME value '" + idNumber + "' in table " + classDefinition.getTableName() + " is not unique.");
            }
        } else {
            // If this is not because of the fact that it's a composite id, an id field is missing and foreign key constraints might fail.
            createdInstances.put(((Double) sheet.getCellValueAt(rowPosition, 0)).intValue(), excelRow);
        }
    }

    /**
     * Stores an Excelrecord by ColumnDefinition.
     * @param sheet Excel file
     * @param classDefinition ClassDefintiion
     * @param rowPosition The number that represents the current row (0 based!)
     * @param excelRow The excelRecord to save the data to
     * @throws NoSuchFieldException Thrown if field cannot be found
     */
    private static void storeExcelRecordByColumnDefinitions(final Workbook excel, final ClassDefinition<?> classDefinition, Integer rowPosition, ExcelRow excelRow)
            throws NoSuchFieldException {
        List<ColumnDefinition> columnDefinitions = classDefinition.getColumnDefinitions();
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            StoreExcelRecordValue.storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
        }
    }
}
