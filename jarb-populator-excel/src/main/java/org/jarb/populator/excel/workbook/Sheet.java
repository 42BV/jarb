package org.jarb.populator.excel.workbook;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Sheet implements Iterable<Row> {
    private final TreeMap<Integer, Row> rows = new TreeMap<Integer, Row>();
    private String name;

    public Sheet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Collection<Row> getRows() {
        return Collections.unmodifiableCollection(rows.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Row> iterator() {
        return getRows().iterator();
    }

    public int getLastRowNumber() {
        return rows.isEmpty() ? 0 : rows.lastKey();
    }

    public Row getRowAt(int rowNo) {
        Row row = rows.get(rowNo);
        if (row == null) {
            row = new Row();
        }
        return row;
    }

    public void setRow(int rowNo, Row row) {
        rows.put(rowNo, row);
    }

    public int addRow(Row row) {
        int rowNo = rows.isEmpty() ? 0 : rows.lastKey() + 1;
        setRow(rowNo, row);
        return rowNo;
    }

    public Cell getCellAt(int rowNo, int colNo) {
        return getRowAt(rowNo).getCellAt(colNo);
    }

    public Object getCellValueAt(int rowNo, int colNo) {
        return getRowAt(rowNo).getCellValueAt(colNo);
    }

    public Row getColumnRow() {
        return getRowAt(0);
    }

    // Column

    /**
     * Retrieve the column name at a specific index.
     * @return column name, if any
     */
    public String getColumnNameAt(int colNo) {
        return getColumnRow().getCellAt(colNo).getValueAsString();
    }

    /**
     * Returned an alphabetic ordered set of all column names.
     * Note that they are in alphabetic order, not in the actual
     * sheet order, and duplicates / null pointers are removed.
     * @return set of column names
     */
    public Set<String> getColumnNames() {
        Set<String> columnNames = new TreeSet<String>();
        for (Cell cell : getColumnRow()) {
            String columnName = cell.getValueAsString();
            if (columnName != null) {
                columnNames.add(columnName);
            }
        }
        return columnNames;
    }

    /**
     * Retrieve the index where a certain column first occurs.
     * @param columnName name of the column index to find
     * @return index of the column's first occurance
     */
    public int indexOfColumn(String columnName) {
        int index = -1; // Return '-1' when nothing can be found
        final int lastColumnIndex = getColumnRow().getLastCellNumber();
        // Loop over each cell, even the empty ones
        for (int columnNo = 0; columnNo <= lastColumnIndex; columnNo++) {
            if (columnName.equals(getCellAt(0, columnNo).getValueAsString())) {
                index = columnNo;
                break; // Index found
            }
        }
        return index;
    }

    /**
     * Determine whether a column exists in this sheet.
     * @param columnName name of the column to find
     * @return {@code true} if the column was found, else {@code false}
     */
    public boolean containsColumn(String columnName) {
        return getColumnNames().contains(columnName);
    }

    /**
     * Retrieve the cell at a specific row index and column.
     * @param rowNo index of the row containing our cell
     * @param columnName name of the column that describes our cell
     * @return desired cell value, never {@code null}
     */
    public Cell getCellAt(int rowNo, String columnName) {
        return getRowAt(rowNo).getCellAt(indexOfColumn(columnName));
    }

    /**
     * Retrieve the cell value at a specific row index and column.
     * @param rowNo index of the row containing our cell
     * @param columnName name of the column that describes our cell
     * @return value of the desired cell, or {@code null} if the cell is empty
     */
    public Object getCellValueAt(int rowNo, String columnName) {
        return getCellAt(rowNo, columnName).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Sheet '" + name + "' " + rows.toString();
    }

}
