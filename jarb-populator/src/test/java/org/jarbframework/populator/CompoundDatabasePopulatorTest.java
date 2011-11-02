package org.jarbframework.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.CannotReadScriptException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class CompoundDatabasePopulatorTest {

    @Autowired
    private DataSource dataSource;

    /**
     * Create schema populator should run before the row insertions.
     */
    @Test
    public void testPopulate() throws Exception {
        new CompoundDatabasePopulator(
            Arrays.asList(
                fromScript("create-schema.sql"),
                fromScript("insert-person.sql")
            )
        ).populate();

        JdbcTemplate template = new JdbcTemplate(dataSource);
        assertEquals("eddie", template.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
    }

    @Test
    public void testSupressExceptions() throws Exception {
        CompoundDatabasePopulator populator = new CompoundDatabasePopulator(
            Arrays.asList(
                fromScript("unknown.sql"),
                fromScript("create-schema.sql"),
                fromScript("unknown.sql"),
                fromScript("insert-person.sql"),
                fromScript("unknown.sql")
            )
        );

        // Just to demonstrate that an exception will be thrown
        try {
            populator.populate();
            fail("Expected an exception because unknown.sql does not exist.");
        } catch(CannotReadScriptException e) {
            assertEquals("Cannot read SQL script from class path resource [unknown.sql]", e.getMessage());
        }

        // Now supress the exception, allowing other populators to also execute
        populator.setContinueOnException(true);
        populator.populate();

        JdbcTemplate template = new JdbcTemplate(dataSource);
        assertEquals("eddie", template.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
    }

    private DatabasePopulator fromScript(String name) {
        SqlResourceDatabasePopulator populator = new SqlResourceDatabasePopulator();
        populator.setDataSource(dataSource);
        populator.setSqlResource(new ClassPathResource(name));
        return populator;
    }

}
