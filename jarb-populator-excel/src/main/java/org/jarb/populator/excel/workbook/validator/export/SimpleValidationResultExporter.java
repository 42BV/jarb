package org.jarb.populator.excel.workbook.validator.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.jarb.populator.excel.workbook.validator.ValidationResult;

/**
 * Exports a {@link ValidationResult} into the provided output stream.
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class SimpleValidationResultExporter {

    /**
     * Perform the validation result export into our output stream.
     * @param validation result of our validation
     * @param os stream being written to
     */
    public void export(ValidationResult validation, OutputStream os) {
        Writer writer = new OutputStreamWriter(os);
        try {
            final Collection<String> messages = validation.getMessages();
            writer.write("Retrieved " + messages.size() + " validation messages:\n");
            for (String message : messages) {
                writer.write(" - " + message + "\n");
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

}
