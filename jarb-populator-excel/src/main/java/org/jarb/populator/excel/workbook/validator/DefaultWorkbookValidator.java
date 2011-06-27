package org.jarb.populator.excel.workbook.validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.MetaModel;
import org.jarb.populator.excel.metamodel.PropertyDatabaseType;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.workbook.Row;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;

/**
 * Default implementation of {@link WorkbookValidator}.
 * 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class DefaultWorkbookValidator implements WorkbookValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkbookValidationResult validate(Workbook workbook, MetaModel metamodel) {
        MutableWorkbookValidationResult result = new MutableWorkbookValidationResult();
        WorkbookExpectation expectation = new WorkbookExpectation(metamodel);
        // Ensure that each definition has a corresponding sheet
        for(String expectedSheetName : expectation.getSheetNames()) {
            if(workbook.containsSheet(expectedSheetName)) {
                Sheet sheet = workbook.getSheet(expectedSheetName);
                validateSheet(sheet, expectation, result);
            } else {
                result.addGlobalViolation(new MissingSheetViolation(expectedSheetName));
            }
        }
        // Ensure that each sheet maps to a definition in our metamodel
        for(String sheetName : workbook.getSheetNames()) {
            if(!expectation.isExpectedSheet(sheetName)) {
                result.addGlobalViolation(new UnknownSheetViolation(sheetName));
            }
        }
        return result;
    }
    
    /**
     * Validate a specific workbook sheet, and append violations in the validation result.
     * @param sheet the sheet being validated
     * @param expectedColumnNames all column names expected in the sheet
     * @param result validation result that contains our violations
     */
    private void validateSheet(Sheet sheet, WorkbookExpectation expectation, MutableWorkbookValidationResult result) {
        final String sheetName = sheet.getName();
        Set<String> expectedColumnNames = expectation.getColumnNames(sheetName);
        // Ensure that each expected column is defined in our sheet
        for(String expectedColumnName : expectedColumnNames) {
            if(!sheet.containsColumn(expectedColumnName)) {
                result.addSheetViolation(sheetName, new MissingColumnViolation(sheetName, expectedColumnName));
            }
        }
        // Ensure that each defined column is acceptable
        for(String columnName : sheet.getColumnNames()) {
            // Our first cell can have any value as it represents the row identifier
            if(!expectedColumnNames.contains(columnName) && sheet.indexOfColumn(columnName) != 0) {
                result.addSheetViolation(sheetName, new UnknownColumnViolation(sheetName, columnName));
            }
        }
        // Ensure that each row has an identifier
        for(Row row : sheet.getRows()) {
            if(row.getValueAt(0) == null) {
                result.addSheetViolation(sheetName, new MissingIdentifierViolation(sheetName, row.getRowNo()));
            }
        }
    }
    
    /**
     * Create the expectations of a workbook, based on some metamodel.
     */
    private class WorkbookExpectation {
        /** All expected sheet names **/
        private Set<String> sheetNames = new HashSet<String>();
        /** All expected column names per sheet **/
        private Map<String, Set<String>> columnNamesMap = new HashMap<String, Set<String>>();
        
        /**
         * Construct a new {@link WorkbookExpectation}.
         * @param metamodel the metamodel that describes our entity structure
         */
        public WorkbookExpectation(MetaModel metamodel) {
            for(EntityDefinition<?> entity : metamodel.entities()) {
                final String sheetName = entity.getTableName();
                // Each entity type has a specific sheet name
                sheetNames.add(sheetName);
                Set<String> columnNames = new HashSet<String>();
                if(entity.hasDiscriminatorColumn()) {
                    columnNames.add(entity.getDiscriminatorColumnName());
                }
                for(PropertyDefinition property : entity.properties()) {
                    if(property.getDatabaseType() == PropertyDatabaseType.JOIN_TABLE) {
                        // Join table properties got their own sheet
                        final String joinSheetName = property.getJoinTableName();
                        sheetNames.add(joinSheetName);
                        // With the join and inverse join as columns
                        Set<String> joinColumnNames = new HashSet<String>();
                        joinColumnNames.add(property.getJoinColumnName());
                        joinColumnNames.add(property.getInverseJoinColumnName());
                        columnNamesMap.put(joinSheetName, joinColumnNames);
                    } else {
                        // Regular properties are mapped to a column name
                        columnNames.add(property.getColumnName());
                    }
                }
                columnNamesMap.put(sheetName, columnNames);
            }
        }
        
        /**
         * Retrieve all expected sheet names.
         * @return expected sheet names
         */
        public Set<String> getSheetNames() {
            return Collections.unmodifiableSet(sheetNames);
        }
        
        /**
         * Determine if a certain sheet is expected.
         * @param sheetName name of the sheet
         * @return {@code true} if the sheet is expected, else {@code false}
         */
        public boolean isExpectedSheet(String sheetName) {
            return sheetNames.contains(sheetName);
        }
        
        /**
         * Retrieve all expected column names of a sheet.
         * @param sheetName name of the sheet
         * @return expected column names
         */
        public Set<String> getColumnNames(String sheetName) {
            return Collections.unmodifiableSet(columnNamesMap.get(sheetName));
        }
    }

}
