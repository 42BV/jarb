package org.jarb.populator.excel.workbook.validator;

public class UnknownSheetViolation extends AbstractWorkbookViolation {
    private final String sheetName;
    
    public UnknownSheetViolation(String sheetName) {
        this.sheetName = sheetName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Sheet '" + sheetName + "' does not exist in our mapping.";
    }
}
