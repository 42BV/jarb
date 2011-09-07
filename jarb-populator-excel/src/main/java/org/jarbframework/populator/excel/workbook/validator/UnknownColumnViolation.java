package org.jarbframework.populator.excel.workbook.validator;

public class UnknownColumnViolation extends AbstractWorkbookViolation {
    private final String sheetName;
    private final String columnName;
    
    public UnknownColumnViolation(String sheetName, String columnName) {
        this.sheetName = sheetName;
        this.columnName = columnName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Column '" + sheetName + "." + columnName + "' does not exist in our mapping.";
    }
}
