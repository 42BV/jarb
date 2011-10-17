package org.jarbframework.populator.excel.mapping.importer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.Workbook;
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

    private StoreExcelRecordValue valueStorer;

    public ExcelImporter(ValueConversionService conversionService) {
        valueStorer = new StoreExcelRecordValue(conversionService);
    }

    /**
     * Returns an objectModel hashmap containing ClassDefinitions with their corresponding Excel records, also by calling parseWorksheet listed below.
     * @param excel Excel file in use to be stored in the objectModel
     * @param classDefinitions All classDefinitions to be stored in the objectModel and saved to the database later on
     * @return Hashmap containing all classdefinitions and parsed excel records.
     * @throws InstantiationException Thrown when function is used on a class that cannot be instantiated (abstract or interface)
     * @throws IllegalAccessException Thrown when function does not have access to the definition of the specified class, field, method or constructor 
     * @throws NoSuchFieldException Thrown when a field is not available
     */
    public Map<EntityDefinition<?>, Map<Object, ExcelRow>> parseExcel(Workbook excel, Collection<EntityDefinition<?>> entities) {
        Map<EntityDefinition<?>, Map<Object, ExcelRow>> objectModel = new HashMap<EntityDefinition<?>, Map<Object, ExcelRow>>();

        for (EntityDefinition<?> classDefinition : entities) {
            LOGGER.debug("Importing " + classDefinition.getTableName());
            objectModel.put(classDefinition, parseWorksheet(excel, classDefinition));
        }

        // Create foreign key relations, resulting in a fully prepared instance

        for (Map.Entry<EntityDefinition<?>, Map<Object, ExcelRow>> objectModelEntry : objectModel.entrySet()) {
            for (Map.Entry<Object, ExcelRow> excelRowEntry : objectModelEntry.getValue().entrySet()) {
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
    public Map<Object, ExcelRow> parseWorksheet(final Workbook excel, final EntityDefinition<?> classDefinition) {
        Map<Object, ExcelRow> createdInstances = new HashMap<Object, ExcelRow>();

        Sheet sheet = excel.getSheet(classDefinition.getTableName());
        String discriminatorColumnName = classDefinition.getDiscriminatorColumnName();
        for (Integer rowPosition = 1; rowPosition <= sheet.getLastRowNumber(); rowPosition++) {
            LOGGER.debug("Importing row {}", rowPosition);
            ExcelRow excelRow = new ExcelRow(determineEntityClass(sheet, classDefinition, discriminatorColumnName, rowPosition));
            storeExcelRecordByColumnDefinitions(excel, classDefinition, rowPosition, excelRow);
            putCreatedInstance(sheet, classDefinition, createdInstances, rowPosition, excelRow);
        }
        return createdInstances;
    }

    private Class<?> determineEntityClass(final Sheet sheet, final EntityDefinition<?> classDefinition, String discriminatorColumnName, Integer rowPosition) {
        Class<?> entityClass = classDefinition.getEntityClass();
        if (discriminatorColumnName != null) {
            WorksheetDefinition worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, sheet.getWorkbook());
            Integer discriminatorPosition = worksheetDefinition.getColumnPosition(discriminatorColumnName);
            String discriminatorValue = getDiscriminatorValueFromExcelFile(sheet, rowPosition, discriminatorPosition);
            if (discriminatorValue != null) {
                Class<?> subClass = classDefinition.getEntitySubClass(discriminatorValue);
                if (subClass != null) {
                    entityClass = subClass;
                }
            }
        }
        return entityClass;
    }

    private String getDiscriminatorValueFromExcelFile(final Sheet sheet, Integer rowPosition, Integer discriminatorPosition) {
        String discriminatorValue = null;
        if (discriminatorPosition != null) {
            discriminatorValue = (String) sheet.getValueAt(rowPosition, discriminatorPosition);
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
    private void putCreatedInstance(final Sheet sheet, final EntityDefinition<?> classDefinition, Map<Object, ExcelRow> createdInstances, Integer rowPosition,
            ExcelRow excelRow) {
        Object identifier = sheet.getValueAt(rowPosition, 0);
        if (identifier == null) {
            LOGGER.error("Could not store row #{} of {}, because the identifier is empty.", new Object[] { rowPosition, sheet.getName() });
        } else {
            if (!createdInstances.containsKey(identifier)) {
                createdInstances.put(identifier, excelRow);
            } else {
                LOGGER.error("IDCOLUMNNAME value '" + identifier + "' in table " + classDefinition.getTableName() + " is not unique.");
            }
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
    private void storeExcelRecordByColumnDefinitions(final Workbook excel, final EntityDefinition<?> classDefinition, Integer rowPosition, ExcelRow excelRow) {
        for (PropertyDefinition columnDefinition : classDefinition.properties()) {
            valueStorer.storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
        }
    }
}
