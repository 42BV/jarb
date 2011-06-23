package org.jarb.populator.excel;

import org.jarb.populator.excel.workbook.validator.WorkbookValidation;

public class InvalidWorkbookException extends RuntimeException {
    private static final long serialVersionUID = -2057031583220142988L;
    private final WorkbookValidation validation;
    
    public InvalidWorkbookException(WorkbookValidation validation) {
        this.validation = validation;
    }
    
    public WorkbookValidation getValidation() {
        return validation;
    }

}
