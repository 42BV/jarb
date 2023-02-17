/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils.jdbc;

import nl._42.jarb.utils.UtilsTestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

/**
 * Ensures that the database product can correctly be recognized.
 *
 * @author Jeroen van Schagen
 * @since Mar 13, 2014
 */
@SpringBootTest(classes = UtilsTestConfig.class)
public class DatabaseProductTest {
    
    @Autowired
    private DataSource dataSource;

    @Test
    public void testFromDataSource() {
        DatabaseProduct product = DatabaseProduct.fromDataSource(dataSource);
        Assertions.assertEquals("HSQL Database Engine", product.getName());
        Assertions.assertEquals("2.7.1", product.getVersion());
    }

}
