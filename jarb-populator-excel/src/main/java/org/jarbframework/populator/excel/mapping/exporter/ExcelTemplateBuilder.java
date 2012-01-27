package org.jarbframework.populator.excel.mapping.exporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.InverseJoinColumnReferenceProperties;
import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.metamodel.PropertyDatabaseType;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.util.JpaUtils;
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

    private static final String ROW_IDENTIFIER_COLUMN_NAME = "#";

    /**
     * Create an empty Excel workbook, with all sheets and columns in place.
     * @param metamodel the metamodel that describes our entity structure
     * @return empty Excel workbook with all sheets and columns
     */
    public Workbook createTemplate(MetaModel metamodel) {
        Workbook workbook = new Workbook();
        List<EntityDefinition<?>> classDefinitions = new ArrayList<EntityDefinition<?>>(metamodel.entities());
        Collections.sort(classDefinitions, new DefinitionNameComparator());
        for (EntityDefinition<?> classDefinition : classDefinitions) {
            createClassSheet(classDefinition, workbook, metamodel);
        }
        return workbook;
    }

    /**
     * Create the sheet for a specific type of entity.
     * @param entityDefinition description of the entity structure being stored
     * @param workbook the workbook that will hold our sheet
     */
    private void createClassSheet(EntityDefinition<?> entityDefinition, Workbook workbook, MetaModel metamodel) {
        Sheet sheet = workbook.createSheet(entityDefinition.getTableName());
        storeColumnNames(sheet, entityDefinition.getColumnNames());
        for (PropertyDefinition propertyDefinition : entityDefinition.properties()) {
            if (propertyDefinition.getDatabaseType() == PropertyDatabaseType.COLLECTION_REFERENCE) {
                createJoinSheet(propertyDefinition, workbook);
            } else if (propertyDefinition.getDatabaseType() == PropertyDatabaseType.INVERSED_REFERENCE) {
                createInversedReferenceSheet(propertyDefinition, workbook, metamodel);
            }
        }
    }

    /**
     * Store all column names in the sheet.
     * @param sheet the sheet that should contain our columns
     * @param ColumnNames ColumnNames to write to Sheet
     */
    private void storeColumnNames(Sheet sheet, Set<String> columnNames) {
        int columnNumber = 0;
        sheet.setColumnNameAt(columnNumber++, ROW_IDENTIFIER_COLUMN_NAME); // Row identifier
        for (String columnName : columnNames) {
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

    private void createInversedReferenceSheet(PropertyDefinition propertyDefinition, Workbook workbook, MetaModel metamodel) {
        InverseJoinColumnReferenceProperties inverseJoinColumnReferenceProperties = propertyDefinition.getInverseJoinColumnReferenceProperties();
        String inversedReferenceTableName = inverseJoinColumnReferenceProperties.getReferencedTableName();

        Sheet joinSheet = workbook.createSheet(inversedReferenceTableName);
        Set<String> inversedReferenceColumns = new HashSet<String>();
        inversedReferenceColumns.addAll(inverseJoinColumnReferenceProperties.getJoinColumnNames());
        inversedReferenceColumns.addAll(JpaUtils.getElementCollectionColumnNames(propertyDefinition, metamodel));
        storeColumnNames(joinSheet, inversedReferenceColumns);
    }

    // Sorts class definitions based on table name
    private static class DefinitionNameComparator implements Comparator<EntityDefinition<?>> {
        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(EntityDefinition<?> left, EntityDefinition<?> right) {
            return left.getTableName().compareTo(right.getTableName());
        }
    }

}
