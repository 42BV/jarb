package org.jarbframework.populator.excel.workbook;

public class EmptyValue implements CellValue {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue() {
        return null;
    }

}
