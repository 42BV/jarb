package org.jarbframework.populator.excel.workbook.validator.export;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.workbook.validator.WorkbookValidationResult;
import org.junit.Test;

public class SimpleValidationResultExporterTest extends DefaultExcelTestDataCase {

    @Test
    public void testExport() throws FileNotFoundException {
        WorkbookValidationResult validation = getExcelDataManager().load("src/test/resources/Excel.xls").validate();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        validation.export(new SimpleValidationExporter(), os);
        String exportedValidation = os.toString();
        assertEquals("Workbook violations\n", exportedValidation);
    }

    @Test
    public void testExportsWithErrors() throws FileNotFoundException {
        WorkbookValidationResult validation = getExcelDataManager().load("src/test/resources/ExcelVerification/missing_column_and_sheet.xls").validate();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        validation.export(new SimpleValidationExporter(), os);
        String exportedValidation = os.toString();
        String results = "Workbook violations\n - Sheet 'currencies' is missing.\n\nSheet 'departments':\n - Column 'departments.department_id' is missing.\n";
        assertEquals(results, exportedValidation);
    }

}
