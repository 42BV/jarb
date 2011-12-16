package org.jarbframework.populator.excel.entity.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import javax.persistence.EntityManagerFactory;

import org.jarbframework.populator.excel.ExcelDataManager;
import org.jarbframework.populator.excel.ExcelDataManagerFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public class NonTransactionalJpaEntityReaderTest {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private ExcelDataManagerFactory excelDataManagerFactory;

    private ExcelDataManager excelDataManager;

    @Before
    public void setUpExcelTestData() {
        excelDataManager = excelDataManagerFactory.build();
    }
 
    public ExcelDataManager getExcelDataManager() {
        return excelDataManager;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
    
    @Ignore
    @Test
    public void testFetchAll() throws FileNotFoundException {
    	JpaEntityReader entityReader = new JpaEntityReader(getEntityManagerFactory());
        getExcelDataManager().load("src/test/resources/Excel.xls").persist();
        try {
        	entityReader.readAll();
        	fail("IllegalStateException expected: 'No transactional EntityManagerFactory supplied'");
        } catch (IllegalStateException ise){
        	assertEquals("No transactional EntityManagerFactory supplied", ise.getMessage());
        }
    }
}
