package org.jarb.populator.excel.workbook;

import org.springframework.util.Assert;

public class Cell {
    private final Row row;
    private final int colNo;
    private CellValue cellValue;
    
    Cell(Row row, int colNo) {
        this(row, colNo, new EmptyValue());
    }

    Cell(Row row, int colNo, CellValue cellValue) {
        Assert.notNull(row, "Row cannot be null");
        Assert.state(colNo >= 0, "Column number has to be positive");
        Assert.notNull(cellValue, "Cell value cannot be null");
        this.row = row;
        this.colNo = colNo;
        this.cellValue = cellValue;
    }

    public Row getRow() {
        return row;
    }
    
    public int getColNo() {
        return colNo;
    }
    
    public int getRowNo() {
        return row.getRowNo();
    }
    
    public Object getValue() {
        return cellValue.getValue();
    }
    
    public Cell setValue(String text) {
        setCellValue(new StringValue(text));
        return this;
    }

    public String getValueAsString() {
        Object value = getValue();
        return value != null ? value.toString() : null;
    }

    public CellValue getCellValue() {
        return cellValue;
    }

    public void setCellValue(CellValue cellValue) {
        this.cellValue = cellValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String valueAsString = getValueAsString();
        return valueAsString != null ? valueAsString : "";
    }

}
