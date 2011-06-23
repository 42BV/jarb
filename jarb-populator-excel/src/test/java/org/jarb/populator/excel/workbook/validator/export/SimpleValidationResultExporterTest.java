package org.jarb.populator.excel.workbook.validator.export;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.workbook.validator.WorkbookValidation;
import org.junit.Test;

public class SimpleValidationResultExporterTest extends DefaultExcelTestDataCase {

    @Test
    public void testExport() throws FileNotFoundException {
        WorkbookValidation validation = getExcelDataManager().loadWorkbook("src/test/resources/Excel.xls").validate();
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        new SimpleValidationExporter().export(validation, arrayOutputStream);
        String validationExport = arrayOutputStream.toString();
        assertNotNull(validationExport);
    }

}
