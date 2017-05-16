package org.jarbframework.init.populate;

import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.sql.DataSource;

import org.jarbframework.init.InitTestConfig;
import org.jarbframework.init.populate.SqlDirectoryDatabasePopulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InitTestConfig.class)
public class SqlDirectoryDatabasePopulatorTest {

    @Autowired
    private DataSource dataSource;
    
    private SqlDirectoryDatabasePopulator populator;
    
    @Before
    public void setUp() {
        populator = new SqlDirectoryDatabasePopulator(dataSource, new File("src/test/resources/imports"));
    }

    @Test
    public void testPopulate() {
        populator.execute();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        assertEquals(Long.valueOf(2), jdbcTemplate.queryForObject("SELECT COUNT(1) FROM persons", Long.class));
        assertEquals("eddie", jdbcTemplate.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
        assertEquals("fred", jdbcTemplate.queryForObject("SELECT name FROM persons WHERE id = 2", String.class));
    }

}
