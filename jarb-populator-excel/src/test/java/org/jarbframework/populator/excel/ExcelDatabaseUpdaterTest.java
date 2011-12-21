package org.jarbframework.populator.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jarbframework.populator.condition.ConditionalDatabaseUpdater;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.io.ClassPathResource;

import domain.entities.Customer;

public class ExcelDatabaseUpdaterTest extends DefaultExcelTestDataCase {
    private ExcelDatabaseUpdater updater;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUp() {
        updater = new ExcelDatabaseUpdater();
        updater.setExcelResource(new ClassPathResource("Excel.xls"));
        updater.setEntityManagerFactory(getEntityManagerFactory());
    }

    /**
     * Customers defined inside our excel should be added.
     */
    @Test
    public void testPopulate() throws Exception {
        updater.update();
        assertFalse(entityManager.createQuery("from domain.entities.Customer", Customer.class).getResultList().isEmpty());
    }

    @Test
    public void testPopulateWithSpecifiedValueConversionService() throws Exception {
        GenericConversionService genericConversionService = ConversionServiceFactory.createDefaultConversionService();
        ValueConversionService valueConversionService = new ValueConversionService(genericConversionService);
        updater.setValueConversionService(valueConversionService);
        updater.update();
    }

    @Test
    public void testConditionalDatabasePopulator() {
        ConditionalDatabaseUpdater conditionalDatabasePopulator = ExcelDatabaseUpdater.ignoreIfResourceMissing(new ClassPathResource(
                "/src/test/resources/ExcelVerification/missing_sheet.xls"), getEntityManagerFactory());
        assertNotNull(conditionalDatabasePopulator);
    }

    @Test
    public void testToString() {
        assertEquals("Excel populator 'class path resource [Excel.xls]'", updater.toString());
    }

}
