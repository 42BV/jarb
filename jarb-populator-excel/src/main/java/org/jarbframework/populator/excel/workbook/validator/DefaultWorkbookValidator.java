package org.jarbframework.populator.excel.workbook.validator;

import org.jarbframework.populator.excel.metamodel.MetaModel;
import org.jarbframework.populator.excel.workbook.Cell;
import org.jarbframework.populator.excel.workbook.Row;
import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.Workbook;

/**
 * Default implementation of {@link WorkbookValidator}.
 * 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public class DefaultWorkbookValidator implements WorkbookValidator {

    @Override
    public WorkbookValidationResult validate(Workbook workbook, MetaModel metamodel) {
        WorkbookValidationResult result = new WorkbookValidationResult();
        WorkbookExpectation expectation = new WorkbookExpectation(metamodel);
        checkIfEachEntityHasSheet(workbook, expectation, result);
        checkIfEachSheetIsRecognized(workbook, expectation, result);
        for (Sheet sheet : workbook) {
            validateSheet(sheet, expectation, result);
        }
        return result;
    }

    private void checkIfEachEntityHasSheet(Workbook workbook, WorkbookExpectation expectation, WorkbookValidationResult validation) {
        for (String sheetName : expectation.getSheetNames()) {
            if (!workbook.containsSheet(sheetName)) {
                validation.addGlobalViolation(new WorkbookViolation("Sheet '" + sheetName + "' is missing.", ViolationLevel.WARNING));
            }
        }
    }

    private void checkIfEachSheetIsRecognized(Workbook workbook, WorkbookExpectation expectation, WorkbookValidationResult validation) {
        for (String sheetName : workbook.getSheetNames()) {
            if (!expectation.isExpectedSheet(sheetName)) {
                validation.addGlobalViolation(new WorkbookViolation("Sheet '" + sheetName + "' does not exist in our mapping.", ViolationLevel.WARNING));
            }
        }
    }

    private void validateSheet(Sheet sheet, WorkbookExpectation expectation, WorkbookValidationResult validation) {
        checkIfEachPropertyHasColumn(sheet, expectation, validation);
        checkIfEachColumnIsRecognized(sheet, expectation, validation);
        checkIfEachRowHasIdentifier(sheet, validation);
    }

    private void checkIfEachPropertyHasColumn(Sheet sheet, WorkbookExpectation expectation, WorkbookValidationResult validation) {
        for (String columnName : expectation.getColumnNames(sheet.getName())) {
            if (!sheet.containsColumn(columnName)) {
                String message = "Column '" + sheet.getName() + "." + columnName + "' is missing.";
                validation.addSheetViolation(sheet.getName(), new WorkbookViolation(message, ViolationLevel.WARNING));
            }
        }
    }

    private void checkIfEachColumnIsRecognized(Sheet sheet, WorkbookExpectation expectation, WorkbookValidationResult validation) {
        for (Cell cell : sheet.getColumnRow()) {
            String columnName = cell.getValueAsString();
            // Identifier column can have any type of string value
            if (columnName != null) {
                if (!(expectation.isExpectedColumn(sheet.getName(), columnName) || cell.getColNo() == Sheet.IDENTIFIER_COLUMN_NO)) {
                    String message = "Column '" + sheet.getName() + "." + columnName + "' does not exist in our mapping.";
                    validation.addSheetViolation(sheet.getName(), new WorkbookViolation(message, ViolationLevel.WARNING));
                }
            }
        }
    }

    private void checkIfEachRowHasIdentifier(Sheet sheet, WorkbookValidationResult validation) {
        for (Row row : sheet.getRows()) {
            if (sheet.getIdentifierAt(row.getRowNo()) == null) {
                String message = "Row " + row.getRowNo() + " in '" + sheet.getName() + "' has no identifier.";
                validation.addSheetViolation(sheet.getName(), new WorkbookViolation(message, ViolationLevel.ERROR));
            }
        }
    }

}
