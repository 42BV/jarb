/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils.jdbc;

import javax.sql.DataSource;

import nl._42.jarb.utils.UtilsTestConfig;
import nl._42.jarb.utils.jdbc.DatabaseProduct;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Ensures that the database product can correctly be recognized.
 *
 * @author Jeroen van Schagen
 * @since Mar 13, 2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = UtilsTestConfig.class)
public class DatabaseProductTest {
    
    @Autowired
    private DataSource dataSource;

    @Test
    public void testFromDataSource() {
        DatabaseProduct product = DatabaseProduct.fromDataSource(dataSource);
        Assert.assertEquals("HSQL Database Engine", product.getName());
        Assert.assertEquals("2.4.1", product.getVersion());
    }

}
