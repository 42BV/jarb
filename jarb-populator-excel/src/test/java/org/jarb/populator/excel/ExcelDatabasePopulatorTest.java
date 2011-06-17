package org.jarb.populator.excel;

import static org.junit.Assert.assertFalse;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.classic.Session;
import org.hibernate.jdbc.Work;
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
    public void testPopulate() throws SQLException {
        Session session = (Session) entityManager.getDelegate();
        session.doWork(new Work() {
           
            @Override
            public void execute(Connection connection) throws SQLException {
                populator.populate(connection);
            }
            
        });
        assertFalse(entityManager.createQuery("from domain.entities.Customer", Customer.class).getResultList().isEmpty());
    }
    
}
