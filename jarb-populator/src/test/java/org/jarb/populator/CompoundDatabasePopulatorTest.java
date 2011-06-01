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
        populator.add(new SkipableSqlResourceDatabasePopulator(new ClassPathResource("create-schema.sql")));
        populator.add(new SkipableSqlResourceDatabasePopulator(new ClassPathResource("insert-person.sql")));

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
}
