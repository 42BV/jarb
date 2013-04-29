package org.jarbframework.migrations;

import static org.junit.Assert.assertEquals;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class MigrateDataSourceTest {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void migrateAndBuildTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Verifies that the 'persons' table, as described below, is created using our 
     * migrating data source. We assert this by creating a new person record and
     * again retrieving it.
     * 
     * <p>
     * 
     * <pre>
     *  createTable(tableName: "persons") {
     *      column(autoIncrement: true, name: "id", type: "BIGINT") {
     *          constraints(nullable: false, primaryKey: true)
     *      }
     *      column(name: "name", type: "VARCHAR(255)", defaultValue: "henk")
     *  }
     * </pre>
     */
    @Test
    public void testMigrateAndFeed() {
        jdbcTemplate.execute("INSERT INTO persons (id) values (1)");
        assertEquals(
                "henk",
                jdbcTemplate.queryForObject("SELECT name FROM persons WHERE id = 1", String.class)
            );
    }

}
