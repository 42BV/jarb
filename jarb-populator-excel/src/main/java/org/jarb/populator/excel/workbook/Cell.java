package org.jarb.populator.excel.workbook;

public class Cell {
    private CellValue cellValue;

    public Cell(CellValue cellValue) {
        this.cellValue = cellValue;
    }

    public static Cell empty() {
        return new Cell(new EmptyValue());
    }

    public static Cell text(String text) {
        return new Cell(new StringValue(text));
    }

    public Object getValue() {
        return cellValue.getValue();
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
