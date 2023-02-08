/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.init.populate;

import nl._42.jarb.init.InitTestConfig;
import nl._42.jarb.populate.HsqlDatabaseClearer;
import nl._42.jarb.populate.SqlDirectoryDatabasePopulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 
 *
 * @author jeroen
 * @since Apr 14, 2015
 */
@SpringBootTest(classes = InitTestConfig.class)
public class HsqlDatabaseClearerTest {

    @Autowired
    private DataSource dataSource;
    
    private SqlDirectoryDatabasePopulator populator;
    
    private HsqlDatabaseClearer clearer;

    @BeforeEach
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

