package org.jarbframework.populator.excel.workbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarbframework.populator.excel.workbook.Cell;
import org.jarbframework.populator.excel.workbook.CellValue;
import org.jarbframework.populator.excel.workbook.NumericValue;
import org.jarbframework.populator.excel.workbook.StringValue;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.junit.Before;
import org.junit.Test;

public class CellTest {
    private Cell cell;
    private Row row;

    @Before
    public void setUp() {
        row = new Workbook().createSheet("test").createRow();
        cell = row.createCell();
    }

    /**
     * Cell values should be storable and retrievable from a cell.
     */
    @Test
    public void testSetGetCellValue() {
        final CellValue value = new NumericValue(42);
        cell.setCellValue(value);
        assertEquals(value, cell.getCellValue());
        assertEquals(Double.valueOf(42), cell.getValue());
    }

    /**
     * Values can also be retrieved as string, returning a {@code null}
     * when the cell value is empty.
     */
    @Test
    public void testGetCellValueAsString() {
        assertNull(cell.getValueAsString());
        cell.setCellValue(new NumericValue(42));
        assertEquals("42.0", cell.getValueAsString());
    }

    /**
     * The string representation of a cell is its cell value. Whenever
     * there is no cell value, we show a blank string.
     */
    @Test
    public void testToString() {
        assertEquals("", cell.toString());
        cell.setCellValue(new StringValue("test"));
        assertEquals("test", cell.toString());
    }

    /**
     * When a new cell is made, it should be linked to its row properly
     * Since the cell is made by creating a cell from the row stored in the variable
     * row, the row of the cell should be the same as the row it was made from. 
     */
    @Test
    public void testGetRow() {
        assertEquals(row, cell.getRow());
    }

    /**
     * Besides having the same row, the cell and the row should also have the same row number
     */
    @Test
    public void testGetRowNo() {
        assertEquals(row.getRowNo(), cell.getRowNo());
    }
}
