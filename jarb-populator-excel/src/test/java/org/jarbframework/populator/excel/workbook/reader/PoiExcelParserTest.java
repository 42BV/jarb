package org.jarbframework.populator.excel.workbook.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.junit.Test;

public class PoiExcelParserTest {

    @Test
    public void testEachValueType() throws FileNotFoundException {
        Workbook excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/excel/parser/all-types.xlsx"));
        Sheet sheet = excel.getSheet("types");
        assertEquals("types", sheet.getName());

        // String
        assertEquals("lol", sheet.getValueAt(0, 0));

        // Boolean: true
        assertEquals(Boolean.TRUE, sheet.getValueAt(4, 0));

        // Double
        assertEquals(Double.valueOf(42.00), sheet.getValueAt(5, 0));
        assertEquals(Double.valueOf(99.23), sheet.getValueAt(3, 1));

        // Formula: =SUM(B4,A6)
        assertEquals(Double.valueOf(141.23000000000002), sheet.getValueAt(5, 1));

        // Date: 5/5/2011 12:05:24 PM
        Calendar calendar = Calendar.getInstance();
        calendar.set(2011, 4, 5, 12, 5, 24);
        Date dateObject = (Date) sheet.getValueAt(9, 1);
        final DateFormat dateFormat = DateFormat.getDateTimeInstance();
        assertEquals(dateFormat.format(calendar.getTime()), dateFormat.format(dateObject));

        // Non existing
        assertNull(sheet.getValueAt(0, 2));
    }

    @Test
    public void testMultiSheet() throws FileNotFoundException {
        Workbook excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/excel/parser/multi-sheet.xlsx"));
        assertEquals(3, excel.getSheetCount());
        assertTrue(excel.containsSheet("Sheet1"));
        assertTrue(excel.containsSheet("Sheet2"));
        assertTrue(excel.containsSheet("Sheet3"));
    }

    @Test(expected = FileNotFoundException.class)
    public void testIOException() throws FileNotFoundException {
        new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/excel/parser/does-not-exist.xls"));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidFileException() throws FileNotFoundException {
        new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/excel/parser/invalid-type.txt"));
    }

}
