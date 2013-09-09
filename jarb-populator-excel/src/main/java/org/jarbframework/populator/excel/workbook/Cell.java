package org.jarbframework.populator.excel.workbook;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class Cell {
	
    private final Row row;
    
    private final int colNo;
    
    private Object value;

    Cell(Row row, int colNo) {
        this(row, colNo, null);
    }

    Cell(Row row, int colNo, Object value) {
        Assert.notNull(row, "Row cannot be null");
        Assert.state(colNo >= 0, "Column number has to be positive");
        
        this.row = row;
        this.colNo = colNo;
        this.value = value;
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

    public String getValueAsString() {
        return value != null ? value.toString() : null;
    }

    public Object getValue() {
        return value;
    }

    public Cell setValue(Object value) {
        this.value = value;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String valueAsString = getValueAsString();
        return StringUtils.defaultString(valueAsString);
    }

}
