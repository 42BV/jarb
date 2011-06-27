package org.jarb.populator.excel.workbook.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;
import org.junit.Test;

public class PoiExcelParserTest {

    @Test
    public void testEachValueType() throws FileNotFoundException {
        Workbook excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/excel/parser/all-types.xlsx"));
        Sheet sheet = excel.getSheet("types");
        assertEquals("types", sheet.getName());
        // String: "lol"
        assertEquals("lol", sheet.getValueAt(0, 0));
        // Double: 99.23
        assertEquals(Double.valueOf(99.23), sheet.getValueAt(3, 1));
        // Boolean: true
        assertEquals(Boolean.TRUE, sheet.getValueAt(4, 0));
        // Date: 5/5/2011 12:05:24 PM
        Calendar calendar = Calendar.getInstance();
        calendar.set(2011, 4, 5, 12, 5, 24);
        Date dateObject = (Date) sheet.getValueAt(9, 1);
        final DateFormat dateFormat = DateFormat.getDateTimeInstance();
        assertEquals(dateFormat.format(calendar.getTime()), dateFormat.format(dateObject));
        // Formula: =SUM(B4,A6)
        assertEquals("SUM(B4,A6)", sheet.getValueAt(5, 1));
        // Non existing
        assertNull(sheet.getValueAt(0, 2));
    }

    @Test
    public void testMultiSheet() throws FileNotFoundException {
        Workbook excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/excel/parser/multi-sheet.xlsx"));
        assertEquals(3, excel.getSheetCount());
        int sheetNo = 1;
        for (Sheet sheet : excel) {
            final String expectedSheetName = "Sheet" + (sheetNo++);
            assertEquals(expectedSheetName, sheet.getName());
        }
    }

    // Exception handling

    @Test(expected = RuntimeException.class)
    public void testIOException() {
        new PoiWorkbookParser() {
            @Override
            protected org.apache.poi.ss.usermodel.Workbook createWorkbook(InputStream inputStream) throws IOException {
                throw new IOException("Fail");
            }
        }.parse(null);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidFileException() {
        new PoiWorkbookParser() {
            @Override
            protected org.apache.poi.ss.usermodel.Workbook createWorkbook(InputStream inputStream) throws InvalidFormatException {
                throw new InvalidFormatException("Fail");
            }
        }.parse(null);
    }

}
