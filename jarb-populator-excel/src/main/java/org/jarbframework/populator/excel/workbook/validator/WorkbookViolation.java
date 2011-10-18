package org.jarbframework.populator.excel.workbook.validator;

/**
 * Workbook violation.
 * 
 * @author Jeroen van Schagen
 * @since 24-06-2011
 */
public class WorkbookViolation {
    private final String message;
    private final ViolationLevel level;

    public WorkbookViolation(String message, ViolationLevel level) {
        this.message = message;
        this.level = level;
    }

    /**
     * Retrieve the actual violation message.
     * @return violation message
     */
    public String getMessage() {
        return message;
    }

    public ViolationLevel getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
