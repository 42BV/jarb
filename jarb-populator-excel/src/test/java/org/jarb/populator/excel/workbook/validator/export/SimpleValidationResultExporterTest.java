package org.jarb.populator.excel.workbook.validator.export;

import java.io.FileNotFoundException;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.workbook.validator.ValidationResult;
import org.junit.Test;

public class SimpleValidationResultExporterTest extends DefaultExcelTestDataCase {

    @Test
    public void testExport() throws FileNotFoundException {
        ValidationResult result = getExcelTestData().validateSheet("src/test/resources/Excel.xls");
        new SimpleValidationResultExporter().export(result, System.out);
    }

}
