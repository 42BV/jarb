package org.jarb.populator.excel.workbook.validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.PropertyDatabaseType;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.workbook.Row;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Default implementation of {@link ExcelValidator}.
 * 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class DefaultExcelValidator implements ExcelValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkbookValidation validate(Workbook workbook, MetaModel metamodel) {
        MutableWorkbookValidation validation = new MutableWorkbookValidation();
        WorkbookExpectation expectation = new WorkbookExpectation(metamodel);
        // Ensure that each definition has a corresponding sheet
        for(String expectedSheetName : expectation.getSheetNames()) {
            if(workbook.containsSheet(expectedSheetName)) {
                Sheet sheet = workbook.getSheet(expectedSheetName);
                Set<String> expectedColumnNames = expectation.getColumnNames(expectedSheetName);
                validateSheet(sheet, expectedColumnNames, validation);
            } else {
                validation.addGlobalViolation(new MissingSheetViolation(expectedSheetName));
            }
        }
        // Ensure that each sheet maps to a definition in our metamodel
        for(String sheetName : workbook.getSheetNames()) {
            if(!expectation.containsSheet(sheetName)) {
                validation.addGlobalViolation(new UnknownSheetViolation(sheetName));
            }
        }
        return validation;
    }
    
    private void validateSheet(Sheet sheet, Set<String> expectedColumnNames, MutableWorkbookValidation validation) {
        final String sheetName = sheet.getName();
        // Ensure that each expected column is defined in our sheet
        for(String expectedColumnName : expectedColumnNames) {
            if(!sheet.containsColumn(expectedColumnName)) {
                validation.addSheetViolation(sheetName, new MissingColumnViolation(sheetName, expectedColumnName));
            }
        }
        // Ensure that each defined column is acceptable
        for(String columnName : sheet.getColumnNames()) {
            // The first column can have any value as it represents the row identifier
            if(!expectedColumnNames.contains(columnName) && sheet.indexOfColumn(columnName) != 0) {
                validation.addSheetViolation(sheetName, new UnknownColumnViolation(sheetName, columnName));
            }
        }
        // Ensure that each row has an identifier
        for(Row row : sheet.getRows()) {
            if(row.getValueAt(0) == null) {
                validation.addSheetViolation(sheetName, new MissingIdentifierViolation(sheetName, row.getRowNo()));
            }
        }
    }
    
    private class WorkbookExpectation {
        private Set<String> sheetNames = new HashSet<String>();
        private Map<String, Set<String>> columnNamesMap = new HashMap<String, Set<String>>();
        
        public WorkbookExpectation(MetaModel metamodel) {
            for(EntityDefinition<?> classDefinition : metamodel.getClassDefinitions()) {
                final String sheetName = classDefinition.getTableName();
                // Each entity type has a specific sheet name
                sheetNames.add(sheetName);
                Set<String> columnNames = new HashSet<String>();
                if(classDefinition.hasDiscriminatorColumn()) {
                    columnNames.add(classDefinition.getDiscriminatorColumnName());
                }
                for(PropertyDefinition propertyDefinition : classDefinition.getPropertyDefinitions()) {
                    if(propertyDefinition.getDatabaseType() == PropertyDatabaseType.JOIN_TABLE) {
                        // Join table properties got their own sheet
                        final String joinSheetName = propertyDefinition.getJoinTableName();
                        sheetNames.add(joinSheetName);
                        // With the join and inverse join as columns
                        Set<String> joinColumnNames = new HashSet<String>();
                        joinColumnNames.add(propertyDefinition.getJoinColumnName());
                        joinColumnNames.add(propertyDefinition.getInverseJoinColumnName());
                        columnNamesMap.put(joinSheetName, joinColumnNames);
                    } else {
                        // Regular properties are mapped to a column name
                        columnNames.add(propertyDefinition.getColumnName());
                    }
                }
                columnNamesMap.put(sheetName, columnNames);
            }
        }
        
        public Set<String> getSheetNames() {
            return Collections.unmodifiableSet(sheetNames);
        }
        
        public boolean containsSheet(String sheetName) {
            return sheetNames.contains(sheetName);
        }
        
        public Set<String> getColumnNames(String sheetName) {
            return Collections.unmodifiableSet(columnNamesMap.get(sheetName));
        }
    }

}
