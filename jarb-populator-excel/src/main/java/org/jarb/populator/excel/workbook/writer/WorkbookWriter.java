package org.jarb.populator.excel.workbook.writer;

import java.io.OutputStream;

import org.jarb.populator.excel.workbook.Workbook;

/**
 * Write the content of an excel file.
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public interface WorkbookWriter {

    /**
     * Write away the content of our workbook.
     * @param workbook contains the content of our excel
     * @param os output stream on the excel file that we should write to
     */
    void write(Workbook workbook, OutputStream os);

}
