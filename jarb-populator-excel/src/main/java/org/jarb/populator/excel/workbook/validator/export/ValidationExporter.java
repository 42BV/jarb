package org.jarb.populator.excel.workbook.validator.export;

import java.io.OutputStream;

import org.jarb.populator.excel.workbook.validator.WorkbookValidation;

public interface ValidationExporter {

    void export(WorkbookValidation validation, OutputStream os);
    
}
