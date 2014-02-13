package org.jarbframework.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
@ContextConfiguration(locations = "classpath:jdbc-context.xml")
public class SqlResourceDatabasePopulatorTest {

    @Autowired
    private DataSource dataSource;

    /**
     * Correct script resources can be executed.
     */
    @Test
    public void testPopulate() {
        new SqlDatabasePopulator(dataSource, new ClassPathResource("import.sql")).populate();
        SqlDatabasePopulator.fromSql(dataSource, "INSERT INTO persons (id,name) VALUES (2,'fred') ;").populate();

        // Ensure the 'persons' table is created, and a record is inserted
        JdbcTemplate template = new JdbcTemplate(dataSource);
        assertEquals("eddie", template.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
        assertEquals("fred", template.queryForObject("SELECT name FROM persons WHERE id = 2", String.class));
    }

    /**
     * Executing with a non-existing script resource causes a runtime exception to be thrown.
     */
    @Test
    public void testFailIfScriptNotFound() {
        try {
            new SqlDatabasePopulator(dataSource, new ClassPathResource("unknown.sql")).populate();
            fail("Expected an exception because unknown.sql does not exist.");
        } catch (CannotReadScriptException e) {
            assertEquals("Cannot read SQL script from class path resource [unknown.sql]", e.getMessage());
        }
    }

}
