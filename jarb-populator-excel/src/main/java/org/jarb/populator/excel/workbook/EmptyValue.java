package org.jarb.populator.excel.workbook;

public class EmptyValue implements CellValue {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue() {
        return null;
    }

}
