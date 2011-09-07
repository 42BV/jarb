package org.jarbframework.populator.excel.workbook;

public class FormulaValue implements CellValue {
    private final String formula;

    public FormulaValue(String formula) {
        this.formula = formula;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return getFormula();
    }

    public String getFormula() {
        return formula;
    }

}
