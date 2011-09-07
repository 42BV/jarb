package org.jarbframework.populator.excel.workbook;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

import org.springframework.util.Assert;

public class Row implements Iterable<Cell> {
    private final Sheet sheet;
    private final int rowNo;
    
    private TreeMap<Integer, Cell> cells = new TreeMap<Integer, Cell>();
    
    Row(Sheet sheet, int rowNo) {
        Assert.notNull(sheet, "Sheet cannot be null");
        Assert.state(rowNo >= 0, "Row number has to be positive");
        this.sheet = sheet;
        this.rowNo = rowNo;
    }
    
    public Sheet getSheet() {
        return sheet;
    }
    
    public int getRowNo() {
        return rowNo;
    }

    public Collection<Cell> getCells() {
        return Collections.unmodifiableCollection(cells.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Cell> iterator() {
        return getCells().iterator();
    }

    public Cell getCellAt(int colNo) {
        Cell cell = cells.get(colNo);
        if (cell == null) {
            cell = createCell(colNo);
        }
        return cell;
    }

    public Object getValueAt(int colNo) {
        return getCellAt(colNo).getValue();
    }
    
    public Cell getCellAt(String columnName) {
        return sheet.getCellAt(rowNo, columnName);
    }
    
    public Object getValueAt(String columnName) {
        return sheet.getValueAt(rowNo, columnName);
    }
    
    public Row setCellValueAt(int colNo, CellValue cellValue) {
        getCellAt(colNo).setCellValue(cellValue);
        return this;
    }
    
    public Row setCellValueAt(String columnName, CellValue cellValue) {
        getCellAt(columnName).setCellValue(cellValue);
        return this;
    }

    public Cell createCell() {
        int colNo = cells.isEmpty() ? 0 : cells.lastKey() + 1;
        return createCell(colNo);
    }
    
    private Cell createCell(int colNo) {
        Cell cell = new Cell(this, colNo);
        cells.put(colNo, cell);
        return cell;
    }

    public int getLastCellNumber() {
        return cells.isEmpty() ? 0 : cells.lastKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return cells.toString();
    }

}
