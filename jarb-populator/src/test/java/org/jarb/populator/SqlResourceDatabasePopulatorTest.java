package org.jarb.populator;

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
public class SqlResourceDatabasePopulatorTest {

    @Autowired
    private DataSource dataSource;

    /**
     * Correct script resources can be executed.
     */
    @Test
    public void testPopulate() throws Exception {
        fromScript("create-schema.sql").populate();
        fromScript("insert-person.sql").populate();

        // Ensure the 'persons' table is created, and a record is inserted
        JdbcTemplate template = new JdbcTemplate(dataSource);
        assertEquals("eddie", template.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
    }
    
    /**
     * Executing with a non-existing script resource causes a runtime exception to be thrown.
     */
    @Test
    public void testFailIfScriptNotFound() throws Exception {
        try {
            fromScript("unknown.sql").populate();
            fail("Expected an exception because unknown.sql does not exist.");
        } catch(CannotReadScriptException e) {
            assertEquals("Cannot read SQL script from class path resource [unknown.sql]", e.getMessage());
        }
    }
    
    @Test
    public void testToString() {
        assertEquals("SQL populator 'class path resource [create-schema.sql]'", fromScript("create-schema.sql").toString());
    }
    
    private SqlResourceDatabasePopulator fromScript(String name) {
        SqlResourceDatabasePopulator populator = new SqlResourceDatabasePopulator();
        populator.setDataSource(dataSource);
        populator.setSqlResource(new ClassPathResource(name));
        return populator;
    }

}
