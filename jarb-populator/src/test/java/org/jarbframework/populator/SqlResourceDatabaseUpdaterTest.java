package org.jarbframework.populator;

import static org.jarbframework.populator.SqlResourceDatabaseUpdater.ignoreIfResourceMissing;
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
@ContextConfiguration(locations = "classpath:application-context.xml")
public class SqlResourceDatabaseUpdaterTest {

    @Autowired
    private DataSource dataSource;

    /**
     * Correct script resources can be executed.
     */
    @Test
    public void testPopulate() {
        script("create-schema.sql").update();
        script("insert-person.sql").update();

        // Ensure the 'persons' table is created, and a record is inserted
        JdbcTemplate template = new JdbcTemplate(dataSource);
        assertEquals("eddie", template.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
    }
    
    /**
     * Executing with a non-existing script resource causes a runtime exception to be thrown.
     */
    @Test
    public void testFailIfScriptNotFound() {
        try {
            script("unknown.sql").update();
            fail("Expected an exception because unknown.sql does not exist.");
        } catch(CannotReadScriptException e) {
            assertEquals("Cannot read SQL script from class path resource [unknown.sql]", e.getMessage());
        }
    }
    
    /**
     * However, we can also create a database populator that skips non existing script resources,
     * preventing any runtime exception from being thrown. Use this type of populator whenever
     * it is not certain if a file will be available.
     */
    @Test
    public void testIgnoreIfScriptNotFound() {
        ignoreIfResourceMissing(new ClassPathResource("unknown.sql"), dataSource).update();
    }
    
    @Test
    public void testToString() {
        assertEquals("SQL(class path resource [create-schema.sql])", script("create-schema.sql").toString());
    }
    
    private SqlResourceDatabaseUpdater script(String name) {
        SqlResourceDatabaseUpdater populator = new SqlResourceDatabaseUpdater();
        populator.setDataSource(dataSource);
        populator.setSqlResource(new ClassPathResource(name));
        return populator;
    }
}
