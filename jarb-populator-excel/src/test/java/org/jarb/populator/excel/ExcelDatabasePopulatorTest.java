package org.jarb.populator.excel;

import static org.junit.Assert.assertFalse;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
        populator.setExcelDataManagerFactory(getExcelDataManagerFactory());
    }

    /**
     * Customers defined inside our excel should be added.
     */
    @Test
    public void testPopulate() throws SQLException {
        populator.populate(null); // Connection is not used
        assertFalse(entityManager.createQuery("from domain.entities.Customer", Customer.class).getResultList().isEmpty());
    }

    /**
     * Resource cannot be null.
     */
    @Test(expected = IllegalStateException.class)
    public void testNullResource() throws SQLException {
        populator.setExcelResource(null);
        populator.populate(null);
    }

    /**
     * Resource has to exist.
     */
    @Test(expected = IllegalStateException.class)
    public void testNonExistingResource() throws SQLException {
        populator.setExcelResource(new ClassPathResource("unknown.xls"));
        populator.populate(null);
    }

    /**
     * Data manager cannot be null.
     */
    @Test(expected = IllegalStateException.class)
    public void testNullExcelDataManager() throws SQLException {
        populator.setExcelDataManager(null);
        populator.populate(null);
    }

}
