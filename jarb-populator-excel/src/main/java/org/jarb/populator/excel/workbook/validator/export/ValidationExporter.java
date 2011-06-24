package org.jarb.populator.excel.workbook.validator.export;

import java.io.OutputStream;

import org.jarb.populator.excel.workbook.validator.MutableWorkbookValidation;

public interface ValidationExporter {

    void export(MutableWorkbookValidation validation, OutputStream os);
    
}
