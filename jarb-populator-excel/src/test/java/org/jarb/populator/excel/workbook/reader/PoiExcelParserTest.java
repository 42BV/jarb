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
        Workbook excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/excel/parser/all-types.xlsx"));
        Sheet sheet = excel.getSheetAt(0);
        assertEquals("types", sheet.getName());
        // String: "lol"
        assertEquals("lol", sheet.getCellValueAt(0, 0));
        // Double: 99.23
        assertEquals(Double.valueOf(99.23), sheet.getCellValueAt(3, 1));
        // Boolean: true
        assertEquals(Boolean.TRUE, sheet.getCellValueAt(4, 0));
        // Date: 5/5/2011 12:05:24 PM
        Calendar calendar = Calendar.getInstance();
        calendar.set(2011, 4, 5, 12, 5, 24);
        Date dateObject = (Date) sheet.getCellValueAt(9, 1);
        final DateFormat dateFormat = DateFormat.getDateTimeInstance();
        assertEquals(dateFormat.format(calendar.getTime()), dateFormat.format(dateObject));
        // Formula: =SUM(B4,A6)
        assertEquals("SUM(B4,A6)", sheet.getCellValueAt(5, 1));
        // Non existing
        assertNull(sheet.getCellValueAt(0, 2));
    }

    @Test
    public void testMultiSheet() throws FileNotFoundException {
        Workbook excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/excel/parser/multi-sheet.xlsx"));
        assertEquals(3, excel.getSheetCount());
        for (int sheetNo = 0; sheetNo < 3; sheetNo++) {
            Sheet sheet = excel.getSheetAt(sheetNo);
            final String expectedSheetName = "Sheet" + (sheetNo + 1);
            assertEquals(expectedSheetName, sheet.getName());
        }
    }

    // Exception handling

    @Test(expected = RuntimeException.class)
    public void testIOException() {
        new PoiExcelParser() {
            @Override
            protected org.apache.poi.ss.usermodel.Workbook createWorkbook(InputStream inputStream) throws IOException {
                throw new IOException("Fail");
            }
        }.parse(null);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidFileException() {
        new PoiExcelParser() {
            @Override
            protected org.apache.poi.ss.usermodel.Workbook createWorkbook(InputStream inputStream) throws InvalidFormatException {
                throw new InvalidFormatException("Fail");
            }
        }.parse(null);
    }

}
