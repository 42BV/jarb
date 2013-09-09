package org.jarbframework.populator.excel.workbook;

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
        sheet = new Workbook().createSheet("test");
    }

    /**
     * Store a row and ensure it can be retrieved again.
     */
    @Test
    public void testRows() {
        final Row row = sheet.createRow();
        // The row is directly retrievable
        assertEquals(row, sheet.getRowAt(row.getRowNo()));
        // Only existing rows are provided in collection
        Collection<Row> rows = sheet.getRows();
        assertEquals(1, rows.size());
        assertTrue(rows.contains(row));
        // The last row number is available
        assertEquals(row.getRowNo(), sheet.getLastRowNumber());
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
        final Cell cell = sheet.getCellAt(0, 0).setValue("42");
        assertEquals(cell, sheet.getCellAt(0, 0));
        assertEquals("42", sheet.getValueAt(0, 0));
    }

    /**
     * Column names can be used to access content in our sheet.
     */
    @Test
    public void testColumns() {
        final Row columnRow = sheet.getRowAt(0);
        columnRow.getCellAt(0).setValue("first");
        columnRow.getCellAt(2).setValue("third");
        columnRow.getCellAt(3).setValue("third");
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
        Row columnRow = sheet.getRowAt(0);
        columnRow.getCellAt(0).setValue("first");
        Row valueRow = sheet.getRowAt(1);
        valueRow.getCellAt(0).setValue("test");
        assertEquals("test", sheet.getValueAt(1, "first"));
        assertNull(sheet.getValueAt(1, "unknown"));
    }

    /**
     * Sheets got a clean string representation.
     */
    @Test
    public void testToString() {
        Row row = sheet.getRowAt(0);
        row.getCellAt(0).setValue("first");
        row.getCellAt(1).setValue("second");
        assertEquals("Sheet 'test' {0={0=first, 1=second}}", sheet.toString());
    }

    /**
     * If a sheet has empty columns, they should be skipped when collecting the column names.
     */
    @Test
    public void testGetColumnNamesWithEmptyColumns() {
        Row row = sheet.getRowAt(0);
        assertTrue(row.getCells().isEmpty());
        row.getCellAt(0).setValue("first");
        row.getCellAt(1);
        row.getCellAt(2).setValue("third");
        Collection<String> headers = sheet.getColumnNames();
        assertTrue(headers.contains("first"));
        assertTrue(headers.contains("third"));
        assertEquals(2, headers.size());
    }

}
