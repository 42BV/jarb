package org.jarb.populator.excel.workbook.validator.export;

import java.io.OutputStream;

import org.jarb.populator.excel.workbook.validator.WorkbookValidationResult;

/**
 * Exports a workbook validation result into an output stream.
 * 
 * @author Jeroen van Schagen
 * @since 11-06-2011
 */
public interface ValidationExporter {

    /**
     * Export a validation result into an output stream.
     * @param validation the validation result being exported
     * @param os output stream being exported into
     */
    void export(WorkbookValidationResult validation, OutputStream os);
    
}
