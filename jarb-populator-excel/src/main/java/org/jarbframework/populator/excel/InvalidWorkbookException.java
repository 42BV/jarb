package org.jarbframework.populator.excel;

import static org.springframework.util.StringUtils.collectionToDelimitedString;

import java.util.Set;

import org.jarbframework.populator.excel.workbook.validator.WorkbookViolation;

/**
 * Exception thrown whenever an action is invoked on an invalid workbook.
 * 
 * @author Jeroen van Schagen
 * @since 23-06-2011
 */
public class InvalidWorkbookException extends RuntimeException {

    public InvalidWorkbookException(Set<WorkbookViolation> violations) {
        super("Workbook is invalid:\n - " + collectionToDelimitedString(violations, "\n - "));
    }

}
