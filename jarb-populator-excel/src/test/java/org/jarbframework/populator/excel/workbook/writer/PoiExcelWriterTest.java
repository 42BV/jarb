package org.jarbframework.populator.excel.workbook.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.workbook.Row;
import org.jarbframework.populator.excel.workbook.Sheet;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.junit.Before;
import org.junit.Test;

public class PoiExcelWriterTest extends DefaultExcelTestDataCase {
	
    private PoiWorkbookWriter writer;
    
    private Workbook workbook;

    @Before
    public void setUpWorkbook() {
        writer = new PoiWorkbookWriter();
        workbook = new Workbook();
    }

    @Test
    public void testSheets() {
        workbook.createSheet("first");
        workbook.createSheet("second");
        workbook.createSheet("third");
        writer.write(workbook, createOutputStream());

        Workbook result = readGeneratedFile();
        assertEquals(3, result.getSheetCount());
        assertTrue(result.containsSheet("first"));
        assertTrue(result.containsSheet("second"));
        assertTrue(result.containsSheet("third"));
    }

    @Test
    public void testString() {
        Sheet sheet = workbook.createSheet("test");
        Row row = sheet.createRow();
        row.getCellAt(0).setValue("haha");
        writer.write(workbook, createOutputStream());

        Sheet result = readGeneratedFile().getSheet("test");
        assertEquals("haha", result.getValueAt(0, 0));
    }

    @Test
    public void testBoolean() {
        Sheet sheet = workbook.createSheet("test");
        Row row = sheet.createRow();
        row.getCellAt(0).setValue(true);
        row.getCellAt(1).setValue(false);
        writer.write(workbook, createOutputStream());

        Sheet result = readGeneratedFile().getSheet("test");
        assertEquals(Boolean.TRUE, result.getValueAt(0, 0));
        assertEquals(Boolean.FALSE, result.getValueAt(0, 1));
    }

    @Test
    public void testDate() {
        Sheet sheet = workbook.createSheet("test");
        Row row = sheet.createRow();
        Date date = new Date();
        row.getCellAt(0).setValue(date);
        writer.write(workbook, createOutputStream());

        Sheet result = readGeneratedFile().getSheet("test");
        assertEquals(date, result.getValueAt(0, 0));
    }

    @Test
    public void testNumeric() {
        Sheet sheet = workbook.createSheet("test");
        Row row = sheet.createRow();
        Double number = Double.valueOf(42);
        row.getCellAt(0).setValue(number);
        writer.write(workbook, createOutputStream());

        Sheet result = readGeneratedFile().getSheet("test");
        assertEquals(number, result.getValueAt(0, 0));
    }

    @Test
    public void testEmpty() {
        Sheet sheet = workbook.createSheet("test");
        Row row = sheet.createRow();
        assertEquals(0, row.getRowNo());
        writer.write(workbook, createOutputStream());

        Sheet result = readGeneratedFile().getSheet("test");
        assertNull(result.getValueAt(0, 0));
    }
}
