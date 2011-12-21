package org.jarbframework.populator;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class CompoundDatabaseUpdaterTest {

    @Autowired
    private DataSource dataSource;

    /**
     * Create schema populator should run before the row insertions.
     */
    @Test
    public void testPopulate() throws Exception {
        new CompositeDatabaseUpdater(
            Arrays.asList(
                fromScript("create-schema.sql"),
                fromScript("insert-person.sql")
            )
        ).update();

        JdbcTemplate template = new JdbcTemplate(dataSource);
        assertEquals("eddie", template.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
    }

    private DatabaseUpdater fromScript(String name) {
        SqlResourceDatabaseUpdater populator = new SqlResourceDatabaseUpdater();
        populator.setDataSource(dataSource);
        populator.setSqlResource(new ClassPathResource(name));
        return populator;
    }
}
