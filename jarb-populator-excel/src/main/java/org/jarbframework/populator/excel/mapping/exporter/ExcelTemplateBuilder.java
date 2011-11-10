package org.jarbframework.populator.excel.mapping.exporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.metamodel.PropertyDatabaseType;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.ColumnMetadataRetriever;
import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.Workbook;

/**
 * Capable of building an empty Excel template, containing a sheet
 * for each type of entity in our meta model. Each property definition
 * with a corresponding column will recieve a seperate Excel column,
 * and each row starts with a row identifier ('#') column.
 * 
 * @author Jeroen van Schagen
 * @since 24-06-2011
 */
public class ExcelTemplateBuilder {

    /**
     * Create an empty Excel workbook, with all sheets and columns in place.
     * @param metamodel the metamodel that describes our entity structure
     * @return empty Excel workbook with all sheets and columns
     */
    public Workbook createTemplate(MetaModel metamodel) {
        Workbook workbook = new Workbook();
        List<Definition<?>> classDefinitions = new ArrayList<Definition<?>>(metamodel.entities());
        Collections.sort(classDefinitions, new DefinitionNameComparator());
        for (Definition<?> classDefinition : classDefinitions) {
            if (classDefinition instanceof EntityDefinition<?>) {
                createClassSheet(classDefinition, workbook);
            }
        }
        return workbook;
    }

    /**
     * Create the sheet for a specific type of entity.
     * @param entityDefinition description of the entity structure being stored
     * @param workbook the workbook that will hold our sheet
     */
    private void createClassSheet(Definition<?> entityDefinition, Workbook workbook) {
        Sheet sheet = workbook.createSheet(entityDefinition.getTableName());
        storeColumnNames(sheet, entityDefinition);
        for (PropertyDefinition propertyDefinition : entityDefinition.properties()) {
            if (propertyDefinition.getDatabaseType() == PropertyDatabaseType.COLLECTION_REFERENCE) {
                createJoinSheet(propertyDefinition, workbook);
            } else if (propertyDefinition.getDatabaseType() == PropertyDatabaseType.ELEMENT_COLLECTION) {
                createElementCollectionSheet(propertyDefinition, workbook);
            }
        }
    }

    /**
     * Store all column names in the sheet.
     * @param sheet the sheet that should contain our columns
     * @param classDefinition description of all columns
     */
    private void storeColumnNames(Sheet sheet, Definition<?> classDefinition) {
        int columnNumber = 0;
        sheet.setColumnNameAt(columnNumber++, "#"); // Row identifier
        for (String columnName : classDefinition.getColumnNames()) {
            sheet.setColumnNameAt(columnNumber++, columnName);
        }
    }

    /**
     * Create the join sheet between two types of entities.
     * @param propertyDefinition definition of the join table property
     * @param workbook the workbook that will hold our sheet
     */
    private void createJoinSheet(PropertyDefinition propertyDefinition, Workbook workbook) {
        Sheet joinSheet = workbook.createSheet(propertyDefinition.getJoinTableName());
        joinSheet.setColumnNameAt(0, propertyDefinition.getJoinColumnName());
        joinSheet.setColumnNameAt(1, propertyDefinition.getInverseJoinColumnName());
    }

    /**
     * Creates a new sheet for an ElementCollection table.
     * @param propertyDefinition definition of the ElementCollection property
     * @param workbook the workbook that will hold our sheet
     */
    private void createElementCollectionSheet(PropertyDefinition propertyDefinition, Workbook workbook) {
        String elementCollectionTableName = propertyDefinition.getName();
        Sheet elementCollectionSheet = workbook.createSheet(elementCollectionTableName);

        Class<?> propertyDefinitionClass = ColumnMetadataRetriever.getCollectionContentsType(propertyDefinition);

        List<String> columns = getElementCollectionColumns(propertyDefinition,
                propertyDefinitionClass);

        for (int columnNumber = 0; columnNumber < columns.size(); ++columnNumber) {
            elementCollectionSheet.setColumnNameAt(columnNumber, columns.get(columnNumber));
        }
    }

    /**
     * Gathers the required columns for an ElementCollection table.
     * @param propertyDefinition definition of the ElementCollection property
     * @param propertyDefinitionClass class to gather the columnnames from
     * @return List of column names
     */
    private List<String> getElementCollectionColumns(PropertyDefinition propertyDefinition, Class<?> propertyDefinitionClass) {
        List<String> columns = new ArrayList<String>();
        columns.addAll(propertyDefinition.getElementCollectionJoinColumnNames());
        columns.addAll(ColumnMetadataRetriever.getColumnNamesForClass(propertyDefinitionClass));
        return columns;
    }

    // Sorts class definitions based on table name
    private static class DefinitionNameComparator implements Comparator<Definition<?>> {
        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(Definition<?> left, Definition<?> right) {
            return left.getTableName().compareTo(right.getTableName());
        }
    }

}
