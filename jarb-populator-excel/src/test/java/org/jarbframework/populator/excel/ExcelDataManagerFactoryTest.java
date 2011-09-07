package org.jarbframework.populator.excel;

import static org.junit.Assert.assertNotNull;

import org.jarbframework.populator.excel.ExcelDataManagerFactory;
import org.junit.Test;

public class ExcelDataManagerFactoryTest extends DefaultExcelTestDataCase {

    /**
     * Build a new test data component, using our factory. Ensure
     * that the factory is not a null pointer.
     */
    @Test
    public void testBuild() {
        assertNotNull(new ExcelDataManagerFactory(getEntityManagerFactory()).build());
    }

}
