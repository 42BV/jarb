package org.jarb.populator.excel.workbook;

import java.util.Date;

public class DateValue implements CellValue {
    private final Date value;

    public DateValue(Date value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getValue() {
        return value;
    }

}
