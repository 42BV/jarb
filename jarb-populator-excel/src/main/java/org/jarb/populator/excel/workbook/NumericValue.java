package org.jarb.populator.excel.workbook;

public class NumericValue implements CellValue {
    private final double value;

    public NumericValue(double value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getValue() {
        return value;
    }

}
