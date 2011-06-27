package org.jarb.populator.excel.workbook.validator.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.jarb.populator.excel.workbook.validator.WorkbookValidationResult;
import org.jarb.populator.excel.workbook.validator.WorkbookViolation;

/**
 * Export the validation result as a simple text.
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class SimpleValidationExporter implements ValidationExporter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void export(WorkbookValidationResult validation, OutputStream os) {
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
