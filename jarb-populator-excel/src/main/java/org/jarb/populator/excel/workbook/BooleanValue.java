package org.jarb.populator.excel.workbook;

public class BooleanValue implements CellValue {
    private final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getValue() {
        return value;
    }

}
