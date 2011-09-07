package org.jarbframework.populator.excel;

import static org.junit.Assert.assertFalse;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jarbframework.populator.excel.ExcelDatabasePopulator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import domain.entities.Customer;

public class ExcelDatabasePopulatorTest extends DefaultExcelTestDataCase {
    private ExcelDatabasePopulator populator;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUp() {
        populator = new ExcelDatabasePopulator();
        populator.setExcelResource(new ClassPathResource("Excel.xls"));
        populator.setEntityManagerFactory(getEntityManagerFactory());
    }

    /**
     * Customers defined inside our excel should be added.
     */
    @Test
    public void testPopulate() throws Exception {
        populator.populate();
        assertFalse(entityManager.createQuery("from domain.entities.Customer", Customer.class).getResultList().isEmpty());
    }
    
}
