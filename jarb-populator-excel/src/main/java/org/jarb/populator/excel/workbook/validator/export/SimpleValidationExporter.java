package org.jarb.populator.excel.workbook.validator.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.jarb.populator.excel.workbook.validator.SheetValidation;
import org.jarb.populator.excel.workbook.validator.WorkbookValidation;

/**
 * Exports a {@link WorkbookValidation} into the provided output stream.
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
    public void export(WorkbookValidation validation, OutputStream os) {
        Writer writer = new OutputStreamWriter(os);
        try {
            writer.write("Missing sheets:\n");
            for (String sheetName : validation.getMissingSheets()) {
                writer.write(" - " + sheetName + "\n");
            }
            writer.write("Unknown sheets:\n");
            for (String sheetName : validation.getUnknownSheets()) {
                writer.write(" - " + sheetName + "\n");
            }
            for(String sheetName : validation.getValidatedSheetNames()) {
                writer.write("\nSheet '" + sheetName + "' validation\n");
                SheetValidation sheetValidation = validation.getSheetValidation(sheetName);
                writer.write("Missing columns:\n");
                for (String columnName : sheetValidation.getMissingColumns()) {
                    writer.write(" - " + columnName + "\n");
                }
                writer.write("Unknown columns:\n");
                for (String columnName : sheetValidation.getUnknownColumns()) {
                    writer.write(" - " + columnName + "\n");
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
