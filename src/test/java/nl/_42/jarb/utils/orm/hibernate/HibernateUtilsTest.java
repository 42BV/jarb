/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils.orm.hibernate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import nl._42.jarb.utils.HibernateConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HibernateConfig.class)
public class HibernateUtilsTest {
    
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
    @Autowired
    private DataSource dataSource;
    
    @Test
    public void testGetDataSource() {
        DataSource providedDataSource = HibernateUtils.getDataSource(entityManagerFactory);
        Assert.assertEquals(dataSource, providedDataSource);
    }

}
