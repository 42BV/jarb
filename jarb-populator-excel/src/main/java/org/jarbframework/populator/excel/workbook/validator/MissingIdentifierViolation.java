package org.jarbframework.populator.excel.workbook.validator;

public class MissingIdentifierViolation extends AbstractWorkbookViolation {
    private final String sheetName;
    private final int rowNo;
    
    public MissingIdentifierViolation(String sheetName, int rowNo) {
        this.sheetName = sheetName;
        this.rowNo = rowNo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Row " + rowNo + " in '" + sheetName + "' has no identifier.";
    }
}
