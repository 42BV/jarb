/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.populate;

import nl._42.jarb.Application;
import nl._42.jarb.populate.PopulateTest.PopulateConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = PopulateConfig.class)
public class PopulateTest {
    
    @Autowired
    private DataSource dataSource;

    @Test
    public void testPopulatedOnStartUp() throws InterruptedException {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        
        // Wait for the asynch populator to kick in
        int attempts = 5;
        while (true) {
            try {
                assertEquals("eddie", template.queryForObject("SELECT name FROM users WHERE id = 1", String.class));
                assertEquals("fred", template.queryForObject("SELECT name FROM users WHERE id = 2", String.class));
                break; // Test successfull, break loop
            } catch (RuntimeException rte) {
                if (--attempts > 0) {
                    Thread.sleep(100);
                } else {
                    throw rte;
                }
            }
        }
    }

    @Configuration
    @Import(Application.class)
    public static class PopulateConfig {

        @Autowired
        private DataSource dataSource;
        
        @Bean
        public PopulatingApplicationListener populateApplicationListener() {
            return new PopulatingApplicationListenerBuilder()
                .initializer()
                    .task()
                        .add(new SqlDatabasePopulator(dataSource, new ClassPathResource("import.sql")))
                        .add(SqlDatabasePopulator.fromSql(dataSource, "INSERT INTO users (id, name) VALUES (2, 'fred') ;"))
                    .current()
                        .add(new SqlDatabasePopulator(dataSource, new ClassPathResource("unknown.sql")).ifExists())
                    .done()
                .destroyer()
                    .build();
        }

    }

}
