package org.jarbframework.populator.excel;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class ExcelDataManagerFactoryBeanTest extends DefaultExcelTestDataCase {

    ExcelDataManagerFactoryBean excelDataManagerFactoryBean;

    @Before
    public void prepareExcelDataManagerFactoryBeanTest() {
        excelDataManagerFactoryBean = new ExcelDataManagerFactoryBean();
        excelDataManagerFactoryBean.setEntityManagerFactory(getEntityManagerFactory());
    }

    @Test
    public void testCreateObject() throws Exception {
        ExcelDataManager excelDataManager = excelDataManagerFactoryBean.createObject();
        assertNotNull(excelDataManager);
    }

}
