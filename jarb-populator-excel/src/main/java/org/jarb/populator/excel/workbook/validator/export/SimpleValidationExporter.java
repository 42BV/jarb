package org.jarb.populator.excel.workbook.validator.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.jarb.populator.excel.workbook.validator.MutableWorkbookValidation;
import org.jarb.populator.excel.workbook.validator.WorkbookViolation;

/**
 * Exports a {@link MutableWorkbookValidation} into the provided output stream.
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class SimpleValidationExporter implements ValidationExporter {

    /**
     * Perform the validation result export into our output stream.
     * @param validation result of our validation
     * @param os stream being written to
     */
    public void export(MutableWorkbookValidation validation, OutputStream os) {
        Writer writer = new OutputStreamWriter(os);
        try {
            writer.write("Workbook violations\n");
            for(WorkbookViolation violation : validation.getGlobalViolations()) {
                writer.write(" - " + violation.getMessage() + "\n");
            }
            for(String sheetName : validation.getValidatedSheetNames()) {
                writer.write("\nSheet '" + sheetName + "':\n");
                for(WorkbookViolation violation : validation.getSheetViolations(sheetName)) {
                    writer.write(" - " + violation.getMessage() + "\n");
                }
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

}
