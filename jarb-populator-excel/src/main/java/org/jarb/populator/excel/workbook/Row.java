package org.jarb.populator.excel.workbook;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

public class Row implements Iterable<Cell> {
    private final TreeMap<Integer, Cell> cells = new TreeMap<Integer, Cell>();

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
            cell = Cell.empty();
        }
        return cell;
    }

    public Object getCellValueAt(int colNo) {
        return getCellAt(colNo).getValue();
    }

    public void setCell(int colNo, Cell cell) {
        cells.put(colNo, cell);
    }

    public int addCell(Cell cell) {
        int colNo = cells.isEmpty() ? 0 : cells.lastKey() + 1;
        setCell(colNo, cell);
        return colNo;
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
