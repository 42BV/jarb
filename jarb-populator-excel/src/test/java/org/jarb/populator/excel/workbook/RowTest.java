package org.jarb.populator.excel.workbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class RowTest {
    private Row row;

    @Before
    public void setUp() {
        row = new Workbook().createSheet("test").createRow();
    }

    /**
     * Cells can be stored and retrieved.
     */
    @Test
    public void testSetAndGetCells() {
        Cell cell = row.getCellAt(2).setCellValue(new StringValue("test"));
        assertEquals(cell, row.getCellAt(2));
        assertTrue(row.getCells().contains(cell));
        assertEquals("test", row.getCellValueAt(2));
        assertEquals(2, row.getLastCellNumber());
    }

    /**
     * Empty cells are created on demand, preventing null pointers.
     */
    @Test
    public void testGetUnknownCell() {
        Cell unknownCell = row.getCellAt(0);
        assertNotNull(unknownCell);
        assertNull(unknownCell.getValue());
        assertNull(row.getCellValueAt(0));
    }

    @Test
    public void testToString() {
        row.getCellAt(2).setCellValue(new StringValue("third"));
        row.getCellAt(0).setCellValue(new StringValue("first"));
        assertEquals("{0=first, 2=third}", row.toString());
    }

}
