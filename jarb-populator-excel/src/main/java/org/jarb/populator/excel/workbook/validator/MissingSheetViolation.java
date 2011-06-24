package org.jarb.populator.excel.workbook.validator;

public class MissingSheetViolation extends AbstractWorkbookViolation {
    private final String sheetName;
    
    public MissingSheetViolation(String sheetName) {
        this.sheetName = sheetName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Sheet '" + sheetName + "' is missing.";
    }
}
