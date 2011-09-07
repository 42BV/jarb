package org.jarbframework.populator.excel.workbook.validator;

/**
 * Violation message, as returned by our validator.
 * 
 * @author Jeroen van Schagn
 * @since 24-06-2011
 */
public interface WorkbookViolation {

    /**
     * Retrieve the actual violation message.
     * @return violation message
     */
    String getMessage();
    
}
