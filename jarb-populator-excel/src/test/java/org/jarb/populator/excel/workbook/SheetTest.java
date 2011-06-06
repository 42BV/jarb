package org.jarb.populator.excel.workbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class SheetTest {
    private Sheet sheet;

    @Before
    public void setUp() {
        sheet = new Sheet("test");
    }

    /**
     * Store a row and ensure it can be retrieved again.
     */
    @Test
    public void testRows() {
        final Row row = new Row();
        sheet.setRow(2, row);
        // The row is directly retrievable
        assertEquals(row, sheet.getRowAt(2));
        // Only existing rows are provided in collection
        Collection<Row> rows = sheet.getRows();
        assertEquals(1, rows.size());
        assertTrue(rows.contains(row));
        // The last row number is available
        assertEquals(2, sheet.getLastRowNumber());
    }

    /**
     * Non existing rows should be created on demand, prevent null pointers. 
     */
    @Test
    public void testUnknownRow() {
        assertNotNull(sheet.getRowAt(42));
    }

    /**
     * Cells should be retrievable directly from our sheet.
     */
    @Test
    public void testGetCell() {
        final Cell cell = Cell.text("42");
        Row row = new Row();
        row.setCell(0, cell);
        sheet.setRow(0, row);
        assertEquals(cell, sheet.getCellAt(0, 0));
        assertEquals("42", sheet.getCellValueAt(0, 0));
    }

    /**
     * Column names can be used to access content in our sheet.
     */
    @Test
    public void testColumns() {
        final Row columnRow = new Row();
        columnRow.setCell(0, Cell.text("first"));
        columnRow.setCell(2, Cell.text("third"));
        columnRow.setCell(3, Cell.text("third"));
        sheet.setRow(0, columnRow);
        // Column row is directly accessable
        assertEquals(columnRow, sheet.getColumnRow());
        // Names are available, duplicates cause no problems
        assertTrue(sheet.containsColumn("first"));
        assertTrue(sheet.containsColumn("third"));
        assertFalse(sheet.containsColumn("unknown"));
        // However, the index is remembered
        assertEquals("third", sheet.getColumnNameAt(2));
        assertEquals("third", sheet.getColumnNameAt(3));
        assertNull(sheet.getColumnNameAt(42));
    }

    /**
     * Cell values can be accessed based on a column name.
     */
    @Test
    public void testGetValueByColumn() {
        Row columnRow = new Row();
        columnRow.setCell(0, Cell.text("first"));
        sheet.setRow(0, columnRow);
        Row valueRow = new Row();
        valueRow.setCell(0, Cell.text("test"));
        sheet.setRow(1, valueRow);
        assertEquals("test", sheet.getCellValueAt(1, "first"));
        assertNull(sheet.getCellValueAt(1, "unknown"));
    }

    /**
     * Sheets got a clean string representation.
     */
    @Test
    public void testToString() {
        Row row = new Row();
        row.setCell(0, Cell.text("first"));
        row.setCell(1, Cell.text("second"));
        sheet.setRow(0, row);
        assertEquals("Sheet 'test' {0={0=first, 1=second}}", sheet.toString());
    }

}
