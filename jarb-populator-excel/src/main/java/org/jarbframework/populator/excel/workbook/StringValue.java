package org.jarbframework.populator.excel.workbook;

public class StringValue implements CellValue {
    private final String value;

    public StringValue(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return value;
    }

}
