package org.jarb.populator.excel.mapping.exporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.PropertyDatabaseType;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;

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
        List<EntityDefinition<?>> classDefinitions = new ArrayList<EntityDefinition<?>>(metamodel.entities());
        Collections.sort(classDefinitions, new ClassDefinitionNameComparator());
        for (EntityDefinition<?> classDefinition : classDefinitions) {
            createClassSheet(classDefinition, workbook);
        }
        return workbook;
    }
    
    /**
     * Create the sheet for a specific type of entity.
     * @param classDefinition description of the entity structure being stored
     * @param workbook the workbook that will hold our sheet
     */
    private void createClassSheet(EntityDefinition<?> classDefinition, Workbook workbook) {
        Sheet sheet = workbook.createSheet(classDefinition.getTableName());
        storeColumnNames(sheet, classDefinition);
        for(PropertyDefinition propertyDefinition : classDefinition.properties()) {
            if(propertyDefinition.getDatabaseType() == PropertyDatabaseType.JOIN_TABLE) {
                createJoinSheet(propertyDefinition, workbook);
            }
        }
    }
    
    /**
     * Store all column names in the sheet.
     * @param sheet the sheet that should contain our columns
     * @param classDefinition description of all columns
     */
    private void storeColumnNames(Sheet sheet, EntityDefinition<?> classDefinition) {
        int columnNumber = 0;
        sheet.setColumnNameAt(columnNumber++, "#"); // Row identifier
        for(String columnName : classDefinition.getColumnNames()) {
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
    
    // Sorts class definitions based on table name
    private static class ClassDefinitionNameComparator implements Comparator<EntityDefinition<?>> {
        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(EntityDefinition<?> left, EntityDefinition<?> right) {
            return left.getTableName().compareTo(right.getTableName());
        }
    }

}
