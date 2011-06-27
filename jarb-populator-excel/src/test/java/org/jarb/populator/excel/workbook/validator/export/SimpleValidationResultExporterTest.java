package org.jarb.populator.excel.workbook.validator.export;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.workbook.validator.WorkbookValidationResult;
import org.junit.Test;

public class SimpleValidationResultExporterTest extends DefaultExcelTestDataCase {

    @Test
    public void testExport() throws FileNotFoundException {
        WorkbookValidationResult validation = getExcelDataManager().loadWorkbook("src/test/resources/Excel.xls").validate();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        validation.export(new SimpleValidationExporter(), os);
        String exportedValidation = os.toString();
        assertNotNull(exportedValidation);
    }

}
