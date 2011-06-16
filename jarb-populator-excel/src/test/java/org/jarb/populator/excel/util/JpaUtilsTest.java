package org.jarb.populator.excel.util;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.junit.Test;

public class JpaUtilsTest extends DefaultExcelTestDataCase {

    @Test
    public void testCreateEntityManager() {
        EntityManager em = JpaUtils.createEntityManager(getEntityManagerFactory());
        assertNotNull(em);
        assertNotNull(em.getProperties());
    }
    
}
