package org.jarb.populator.excel.workbook.validator;

public class MissingColumnViolation extends AbstractWorkbookViolation {
    private final String sheetName;
    private final String columnName;
    
    public MissingColumnViolation(String sheetName, String columnName) {
        this.sheetName = sheetName;
        this.columnName = columnName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Column '" + sheetName + "." + columnName + "' is missing.";
    }
}
