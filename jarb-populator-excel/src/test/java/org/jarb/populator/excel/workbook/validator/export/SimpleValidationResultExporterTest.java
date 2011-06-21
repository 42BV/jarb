package org.jarb.populator.excel.workbook.validator.export;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.workbook.validator.ValidationResult;
import org.junit.Test;

public class SimpleValidationResultExporterTest extends DefaultExcelTestDataCase {

    @Test
    public void testExport() throws FileNotFoundException {
        ValidationResult result = getExcelDataManager().validateWorkbook(new FileInputStream("src/test/resources/Excel.xls"));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new SimpleValidationResultExporter().export(result, os);
        String export = os.toString();
        // Number of validation messages is included
        assertTrue(export.contains("Retrieved 16 validation messages"));
        // Success messages are included
        assertTrue(export.contains("Excel columns in worksheet [workspaces] match the mapping."));
        // Error messages are included
        assertTrue(export.contains("Error in Excel worksheet [employees_projects_workspaces]: Sheet does not contain column [id] present in the mapping."));
        // Sheet validation message is included
        assertTrue(export.contains("All the sheets specified in the mapping are present in the Excel file."));
    }

}
