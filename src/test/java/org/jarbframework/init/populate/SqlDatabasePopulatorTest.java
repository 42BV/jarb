package org.jarbframework.init.populate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.sql.DataSource;

import org.jarbframework.init.InitTestConfig;
import org.jarbframework.populate.SqlDatabasePopulator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InitTestConfig.class)
public class SqlDatabasePopulatorTest {

    @Autowired
    private DataSource dataSource;

    /**
     * Executing with a non-existing script resource causes a runtime exception to be thrown.
     */
    @Test
    public void testFailIfScriptNotFound() {
        try {
            new SqlDatabasePopulator(dataSource, new ClassPathResource("unknown.sql")).execute();
            fail("Expected an exception because unknown.sql does not exist.");
        } catch (IllegalStateException e) {
            assertEquals("Resource 'unknown.sql' does not exist.", e.getMessage());
        }
    }

}
