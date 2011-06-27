package org.jarb.populator.excel.workbook.validator;

import java.io.OutputStream;
import java.util.Set;

import org.jarb.populator.excel.workbook.validator.export.ValidationExporter;

/**
 * Workbook validation result, returned by {@link WorkbookValidator}.
 * 
 * @author Jeroen van Schagen
 * @since 27-06-2011
 */
public interface WorkbookValidationResult {
    
    boolean hasViolations();
    
    Set<WorkbookViolation> getViolations();
    
    Set<WorkbookViolation> getGlobalViolations();
    
    Set<WorkbookViolation> getSheetViolations(String sheetName);
    
    Set<String> getValidatedSheetNames();
    
    void export(ValidationExporter exporter, OutputStream os);
    
}
