package org.jarbframework.populator.excel.util;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.util.JpaUtils;
import org.junit.Test;

public class JpaUtilsTest extends DefaultExcelTestDataCase {

    @Test
    public void testCreateEntityManager() {
        EntityManager em = JpaUtils.createEntityManager(getEntityManagerFactory());
        assertNotNull(em);
        assertNotNull(em.getProperties());
    }
    
}
