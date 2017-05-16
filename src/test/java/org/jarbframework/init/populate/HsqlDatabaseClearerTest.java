/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.init.populate;

import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.sql.DataSource;

import org.jarbframework.init.InitTestConfig;
import org.jarbframework.populate.HsqlDatabaseClearer;
import org.jarbframework.populate.SqlDirectoryDatabasePopulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 *
 * @author jeroen
 * @since Apr 14, 2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InitTestConfig.class)
public class HsqlDatabaseClearerTest {

    @Autowired
    private DataSource dataSource;
    
    private SqlDirectoryDatabasePopulator populator;
    
    private HsqlDatabaseClearer clearer;

    @Before
    public void setUp() {
        populator = new SqlDirectoryDatabasePopulator(dataSource, new File("src/test/resources/imports"));
        clearer = new HsqlDatabaseClearer(dataSource);
    }
    
    @Test
    public void testClear() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        populator.execute();
        assertEquals(Long.valueOf(2), jdbcTemplate.queryForObject("SELECT COUNT(1) FROM persons", Long.class));
        
        clearer.execute();
        assertEquals(Long.valueOf(0), jdbcTemplate.queryForObject("SELECT COUNT(1) FROM persons", Long.class));
    }

}

