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
        checkIfEachEntityHasSheet(workbook, result, expectation);
        checkIfEachSheetIsRecognized(workbook, result, expectation);
        for (Sheet sheet : workbook) {
            validateSheet(sheet, expectation, result);
        }
        return result;
    }

    private void checkIfEachEntityHasSheet(Workbook workbook, WorkbookValidationResult result, WorkbookExpectation expectation) {
        for (String sheetName : expectation.getSheetNames()) {
            if (!workbook.containsSheet(sheetName)) {
                result.addGlobalViolation(new WorkbookViolation("Sheet '" + sheetName + "' is missing.", ViolationLevel.WARNING));
            }
        }
    }

    private void checkIfEachSheetIsRecognized(Workbook workbook, WorkbookValidationResult result, WorkbookExpectation expectation) {
        for (String sheetName : workbook.getSheetNames()) {
            if (!expectation.isExpectedSheet(sheetName)) {
                result.addGlobalViolation(new WorkbookViolation("Sheet '" + sheetName + "' does not exist in our mapping.", ViolationLevel.WARNING));
            }
        }
    }

    private void validateSheet(Sheet sheet, WorkbookExpectation expectation, WorkbookValidationResult result) {
        checkIfEachPropertyHasColumn(sheet, expectation, result);
        checkIfEachColumnIsRecognized(sheet, expectation, result);
        checkIfEachRowHasIdentifier(sheet, result);
    }

    private void checkIfEachPropertyHasColumn(Sheet sheet, WorkbookExpectation expectation, WorkbookValidationResult result) {
        for (String columnName : expectation.getColumnNames(sheet.getName())) {
            if (!sheet.containsColumn(columnName)) {
                String message = "Column '" + sheet.getName() + "." + columnName + "' is missing.";
                result.addSheetViolation(sheet.getName(), new WorkbookViolation(message, ViolationLevel.WARNING));
            }
        }
    }

    private void checkIfEachColumnIsRecognized(Sheet sheet, WorkbookExpectation expectation, WorkbookValidationResult result) {
        for (Cell cell : sheet.getColumnRow()) {
            String columnName = cell.getValueAsString();
            // Identifier column can have any type of string value
            if (!(expectation.isExpectedColumn(sheet.getName(), columnName) || cell.getColNo() == Sheet.IDENTIFIER_COLUMN_NO)) {
                String message = "Column '" + sheet.getName() + "." + columnName + "' does not exist in our mapping.";
                result.addSheetViolation(sheet.getName(), new WorkbookViolation(message, ViolationLevel.WARNING));
            }
        }
    }

    private void checkIfEachRowHasIdentifier(Sheet sheet, WorkbookValidationResult result) {
        for (Row row : sheet.getRows()) {
            if (sheet.getIdentifierAt(row.getRowNo()) == null) {
                String message = "Row " + row.getRowNo() + " in '" + sheet.getName() + "' has no identifier.";
                result.addSheetViolation(sheet.getName(), new WorkbookViolation(message, ViolationLevel.ERROR));
            }
        }
    }

}
