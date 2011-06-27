package org.jarb.populator.excel;

import static org.springframework.util.StringUtils.collectionToDelimitedString;

import org.jarb.populator.excel.workbook.validator.WorkbookValidationResult;

/**
 * Exception thrown whenever an action is invoked on an invalid workbook.
 * 
 * @author Jeroen van Schagen
 * @since 23-06-2011
 */
public class InvalidWorkbookException extends RuntimeException {
    private static final long serialVersionUID = -2057031583220142988L;
    /** Validation result that contains our workbook violations. **/
    private final WorkbookValidationResult validation;
    
    /**
     * Construct a new {@link InvalidWorkbookException}.
     * @param validation validation results with our workbook violations
     */
    public InvalidWorkbookException(WorkbookValidationResult validation) {
        super("Worbook is invalid:\n - " + collectionToDelimitedString(validation.getViolations(), "\n - "));
        this.validation = validation;
    }
    
    /**
     * Retrieve the validation result that contains our workbook violations.
     * @return validation result
     */
    public WorkbookValidationResult getValidation() {
        return validation;
    }
}
