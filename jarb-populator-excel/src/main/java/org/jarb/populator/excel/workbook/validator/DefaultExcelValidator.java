package org.jarb.populator.excel.workbook.validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.ColumnType;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.validator.WorkbookValidation.SheetValidation;

/**
 * Default implementation of {@link ExcelValidator}.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class DefaultExcelValidator implements ExcelValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkbookValidation validate(Workbook workbook, MetaModel metamodel) {
        WorkbookValidation validation = new WorkbookValidation();
        WorkbookExpectation expectation = new WorkbookExpectation(metamodel);
        // Ensure that each sheet maps to a definition in our metamodel
        for(String sheetName : workbook.getSheetNames()) {
            if(expectation.containsSheet(sheetName)) {
                Sheet sheet = workbook.getSheet(sheetName);
                Set<String> expectedColumnNames = expectation.getColumnNames(sheetName);
                validation.includeSheetValidation(sheetName, validateSheet(sheet, expectedColumnNames));
            } else {
                validation.addUnknownSheet(sheetName);   
            }
        }
        // Ensure that each definition has a corresponding sheet
        for(String sheetName : expectation.getSheetNames()) {
            if(!workbook.containsSheet(sheetName)) {
                validation.addMissingSheet(sheetName);
            }
        }
        return validation;
    }
    
    private SheetValidation validateSheet(Sheet sheet, Set<String> expectedColumnNames) {
        SheetValidation validation = new SheetValidation();
        // Ensure that each expected column is defined in our sheet
        for(String expectedColumnName : expectedColumnNames) {
            if(!sheet.containsColumn(expectedColumnName)) {
                validation.addMissingColumn(expectedColumnName);
            }
        }
        // Ensure that each defined column is expected
        for(String columnName : sheet.getColumnNames()) {
            if(!expectedColumnNames.contains(columnName)) {
                validation.addUnknownColumn(columnName);
            }
        }
        return validation;
    }
    
    private class WorkbookExpectation {
        private Set<String> sheetNames = new HashSet<String>();
        private Map<String, Set<String>> columnNamesMap = new HashMap<String, Set<String>>();
        
        public WorkbookExpectation(MetaModel metamodel) {
            for(ClassDefinition<?> classDefinition : metamodel.getClassDefinitions()) {
                final String sheetName = classDefinition.getTableName();
                // Each entity type has a specific sheet name
                sheetNames.add(sheetName);
                Set<String> columnNames = new HashSet<String>();
                columnNames.add("id");
                if(classDefinition.hasDiscriminatorColumn()) {
                    columnNames.add(classDefinition.getDiscriminatorColumnName());
                }
                for(PropertyDefinition propertyDefinition : classDefinition.getPropertyDefinitions()) {
                    if(propertyDefinition.getColumnType() == ColumnType.JOIN_TABLE) {
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
