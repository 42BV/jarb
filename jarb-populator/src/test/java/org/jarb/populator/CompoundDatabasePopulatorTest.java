package org.jarb.populator;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarb.utils.JdbcUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.CannotReadScriptException;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
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
    public void testPopulate() throws SQLException {
        CompoundDatabasePopulator populator = new CompoundDatabasePopulator();
        populator.add(sqlPopulator("create-schema.sql"));
        populator.add(sqlPopulator("insert-person.sql"));

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            populator.populate(connection);

            JdbcTemplate template = new JdbcTemplate(dataSource);
            assertEquals("eddie", template.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
        } finally {
            JdbcUtils.closeQuietly(connection);
        }
    }
    
    @Test
    public void testSupressExceptions() throws SQLException {
        CompoundDatabasePopulator populator = new CompoundDatabasePopulator();
        populator.add(sqlPopulator("unknown.sql")); // Does not exist
        populator.add(sqlPopulator("create-schema.sql"));
        populator.add(sqlPopulator("unknown.sql")); // Does not exist
        populator.add(sqlPopulator("insert-person.sql"));
        populator.add(sqlPopulator("unknown.sql")); // Does not exist

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            
            // Just to demonstrate that an exception will be thrown
            try {
                populator.populate(connection);
            } catch(CannotReadScriptException e) {
                assertEquals("Cannot read SQL script from class path resource [unknown.sql]", e.getMessage());
            }
            
            // Enable our fail safe mechanism, supressing the previously thrown exception
            // and executing other populators further down in the chain
            populator.setContinueOnException(true);
            populator.populate(connection);

            JdbcTemplate template = new JdbcTemplate(dataSource);
            assertEquals("eddie", template.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
        } finally {
            JdbcUtils.closeQuietly(connection);
        }
    }
    
    private DatabasePopulator sqlPopulator(String name) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(name));
        return populator;
    }
    
}
