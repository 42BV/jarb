package org.jarbframework.populator.excel.workbook;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class Sheet implements Iterable<Row> {
    public static final int COLUMN_ROW_NO = 0;
    public static final int IDENTIFIER_COLUMN_NO = 0;

    private final TreeMap<Integer, Row> rows = new TreeMap<Integer, Row>();
    private final Workbook workbook;
    private String name;

    Sheet(Workbook workbook, String name) {
        Assert.notNull(workbook, "Workbook cannot be null");
        Assert.hasText(name, "Sheet needs to have a name");
        this.workbook = workbook;
        this.name = name;
    }

    public Workbook getWorkbook() {
        return workbook;
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
            row = createRow(rowNo);
        }
        return row;
    }

    public Row createRow() {
        int rowNo = rows.isEmpty() ? 0 : rows.lastKey() + 1;
        return createRow(rowNo);
    }

    private Row createRow(int rowNo) {
        Row row = new Row(this, rowNo);
        rows.put(rowNo, row);
        return row;
    }

    public Row getColumnRow() {
        return getRowAt(COLUMN_ROW_NO);
    }

    /**
     * Retrieve the column name at a specific index.
     * @return column name, if any
     */
    public String getColumnNameAt(int colNo) {
        return getColumnRow().getCellAt(colNo).getValueAsString();
    }

    public void setColumnNameAt(int colNo, String columnName) {
        getCellAt(COLUMN_ROW_NO, colNo).setCellValue(new StringValue(columnName));
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
            if (StringUtils.isNotBlank(columnName)) {
                columnNames.add(columnName);
            }
        }
        return columnNames;
    }

    /**
     * Retrieve the index where a certain column first occurs.
     * @param columnName name of the column index to find
     * @return index of the column's first occurrence
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
        if (columnName == null) {
            return false;
        }
        return getColumnNames().contains(columnName);
    }

    /**
     * Retrieve the cell at a specific row index and column.
     * @param rowNo index of the row containing our cell
     * @param columnName name of the column that describes our cell
     * @return desired cell, or {@code null} if no matching column could be found
     */
    public Cell getCellAt(int rowNo, String columnName) {
        int colNo = indexOfColumn(columnName);
        if (colNo == -1) {
            return null;
        }
        return getCellAt(rowNo, colNo);
    }

    /**
     * Retrieve the cell at a specific position.
     * @param rowNo row number
     * @param colNo column number
     * @return cell at the specified position
     */
    public Cell getCellAt(int rowNo, int colNo) {
        return getRowAt(rowNo).getCellAt(colNo);
    }

    /**
     * Retrieve the cell value at a specific row index and column.
     * @param rowNo index of the row containing our cell
     * @param columnName name of the column that describes our cell
     * @return desired cell value, or {@code null} if no matching column could be found
     */
    public Object getValueAt(int rowNo, String columnName) {
        Cell cell = getCellAt(rowNo, columnName);
        return cell != null ? cell.getValue() : null;
    }

    /**
     * Retrieve the cell value at a specific position.
     * @param rowNo row number
     * @param colNo column number
     * @return cell at the specified position
     */
    public Object getValueAt(int rowNo, int colNo) {
        return getRowAt(rowNo).getValueAt(colNo);
    }

    public Object getIdentifierAt(int rowNo) {
        return getValueAt(rowNo, IDENTIFIER_COLUMN_NO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Sheet '" + name + "' " + rows.toString();
    }
}
