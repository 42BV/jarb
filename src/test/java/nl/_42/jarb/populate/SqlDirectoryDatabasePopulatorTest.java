package nl._42.jarb.populate;

import nl._42.jarb.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
public class SqlDirectoryDatabasePopulatorTest {

    @Autowired
    private DataSource dataSource;
    
    private SqlDirectoryDatabasePopulator populator;
    
    @BeforeEach
    public void setUp() {
        populator = new SqlDirectoryDatabasePopulator(dataSource, new File("src/test/resources/imports"));
    }

    @Test
    public void testPopulate() {
        populator.execute();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        assertEquals(Long.valueOf(2), jdbcTemplate.queryForObject("SELECT COUNT(1) FROM users", Long.class));
        assertEquals("eddie", jdbcTemplate.queryForObject("SELECT name FROM users WHERE id = 1", String.class));
        assertEquals("fred", jdbcTemplate.queryForObject("SELECT name FROM users WHERE id = 2", String.class));
    }

}
