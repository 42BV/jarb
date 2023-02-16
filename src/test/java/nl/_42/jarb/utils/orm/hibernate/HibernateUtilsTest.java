/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils.orm.hibernate;

import jakarta.persistence.EntityManagerFactory;
import nl._42.jarb.utils.HibernateConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = HibernateConfig.class)
public class HibernateUtilsTest {
    
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
    @Autowired
    private DataSource dataSource;
    
    @Test
    public void testGetDataSource() {
        DataSource providedDataSource = HibernateUtils.getDataSource(entityManagerFactory);
        assertEquals(dataSource, providedDataSource);
    }

}
