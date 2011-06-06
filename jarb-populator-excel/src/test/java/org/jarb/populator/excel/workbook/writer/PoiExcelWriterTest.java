package org.jarb.populator.excel.workbook.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.workbook.BooleanValue;
import org.jarb.populator.excel.workbook.Cell;
import org.jarb.populator.excel.workbook.DateValue;
import org.jarb.populator.excel.workbook.FormulaValue;
import org.jarb.populator.excel.workbook.NumericValue;
import org.jarb.populator.excel.workbook.Row;
import org.jarb.populator.excel.workbook.Sheet;
import org.jarb.populator.excel.workbook.Workbook;
import org.junit.Before;
import org.junit.Test;

public class PoiExcelWriterTest extends DefaultExcelTestDataCase {
    private PoiExcelWriter writer;
    private Workbook workbook;

    @Before
    public void setUpWorkbook() {
        writer = new PoiExcelWriter();
        workbook = new Workbook();
    }

    @Test
    public void testSheets() {
        workbook.addSheet(new Sheet("first"));
        workbook.addSheet(new Sheet("second"));
        workbook.addSheet(new Sheet("third"));
        writer.write(workbook, createFileOutputStream());
        Workbook result = readGeneratedFile();
        assertEquals(3, result.getSheetCount());
        assertEquals(Arrays.asList("first", "second", "third"), result.getSheetNames());
    }

    @Test
    public void testString() {
        Sheet sheet = new Sheet("test");
        Row row = new Row();
        row.setCell(0, Cell.text("haha"));
        sheet.setRow(0, row);
        workbook.addSheet(sheet);
        writer.write(workbook, createFileOutputStream());
        Sheet result = readGeneratedFile().getSheetAt(0);
        assertEquals("haha", result.getCellValueAt(0, 0));
    }

    @Test
    public void testBoolean() {
        Sheet sheet = new Sheet("test");
        Row row = new Row();
        row.setCell(0, new Cell(new BooleanValue(true)));
        row.setCell(1, new Cell(new BooleanValue(false)));
        sheet.setRow(0, row);
        workbook.addSheet(sheet);
        writer.write(workbook, createFileOutputStream());
        Sheet result = readGeneratedFile().getSheetAt(0);
        assertEquals(Boolean.TRUE, result.getCellValueAt(0, 0));
        assertEquals(Boolean.FALSE, result.getCellValueAt(0, 1));
    }

    @Test
    public void testDate() {
        Sheet sheet = new Sheet("test");
        Row row = new Row();
        Date date = new Date();
        row.setCell(0, new Cell(new DateValue(date)));
        sheet.setRow(0, row);
        workbook.addSheet(sheet);
        writer.write(workbook, createFileOutputStream());
        Sheet result = readGeneratedFile().getSheetAt(0);
        assertEquals(date, result.getCellValueAt(0, 0));
    }

    @Test
    public void testNumeric() {
        Sheet sheet = new Sheet("test");
        Row row = new Row();
        Double number = Double.valueOf(42);
        row.setCell(0, new Cell(new NumericValue(number)));
        sheet.setRow(0, row);
        workbook.addSheet(sheet);
        writer.write(workbook, createFileOutputStream());
        Sheet result = readGeneratedFile().getSheetAt(0);
        assertEquals(number, result.getCellValueAt(0, 0));
    }

    @Test
    public void testFormula() {
        Sheet sheet = new Sheet("test");
        Row row = new Row();
        row.setCell(0, new Cell(new NumericValue(1D)));
        row.setCell(1, new Cell(new NumericValue(2D)));
        row.setCell(2, new Cell(new FormulaValue("SUM(A1:B1)")));
        sheet.setRow(0, row);
        workbook.addSheet(sheet);
        writer.write(workbook, createFileOutputStream());
        Sheet result = readGeneratedFile().getSheetAt(0);
        assertEquals("SUM(A1:B1)", result.getCellValueAt(0, 2));
    }

    @Test
    public void testEmpty() {
        Sheet sheet = new Sheet("test");
        Row row = new Row();
        row.setCell(0, Cell.empty());
        sheet.setRow(0, row);
        workbook.addSheet(sheet);
        writer.write(workbook, createFileOutputStream());
        Sheet result = readGeneratedFile().getSheetAt(0);
        assertNull(result.getCellValueAt(0, 0));
    }
}
