package org.jarb.populator.excel;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ExcelTestDataFactoryTest extends DefaultExcelTestDataCase {

    /**
     * Build a new test data component, using our factory. Ensure
     * that the factory is not a null pointer.
     */
    @Test
    public void testBuild() {
        ExcelTestData etd = ExcelTestDataFactory.build(getEntityManagerFactory());
        assertNotNull(etd);
    }

}
