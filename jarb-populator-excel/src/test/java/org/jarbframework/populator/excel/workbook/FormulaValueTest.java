package org.jarbframework.populator.excel.workbook;

import static org.junit.Assert.assertEquals;

import org.jarbframework.populator.excel.workbook.FormulaValue;
import org.junit.Test;

public class FormulaValueTest {

    /**
     * Assert that our actual formula is returned as value.
     */
    @Test
    public void testValueIsFormula() {
        final String formula = "SUM(B1,B100)";
        FormulaValue formulaValue = new FormulaValue(formula);
        assertEquals(formula, formulaValue.getValue());
    }

}
