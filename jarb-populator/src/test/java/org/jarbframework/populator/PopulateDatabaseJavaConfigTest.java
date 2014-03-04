/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.populator;

import static org.junit.Assert.assertEquals;

import javax.sql.DataSource;

import org.jarbframework.populator.PopulateDatabaseJavaConfigTest.PopulateConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PopulateConfig.class)
public class PopulateDatabaseJavaConfigTest {
    
    @Autowired
    private DataSource dataSource;

    @Test
    public void testPopulatedOnStartUp() throws InterruptedException {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        
        // Wait for the asynch populator to kick in
        int attempts = 5;
        while (true) {
            try {
                assertEquals("eddie", template.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
                assertEquals("fred", template.queryForObject("SELECT name FROM persons WHERE id = 2", String.class));
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
    @ImportResource("classpath:jdbc-context.xml")
    public static class PopulateConfig {

        @Autowired
        private DataSource dataSource;
        
        @Bean
        public PopulateApplicationListener populateApplicationListener() {
            return new PopulateApplicationListenerBuilder()
                        .initializer()
                            .task()
                                .add(new SqlDatabasePopulator(dataSource, new ClassPathResource("import.sql")))
                                .add(SqlDatabasePopulator.fromSql(dataSource, "INSERT INTO persons (id, name) VALUES (2, 'fred') ;"))
                            .current()
                                .add(new SqlDatabasePopulator(dataSource, new ClassPathResource("unknown.sql")).ifExists())
                            .done()
                        .destroyer()
                            .build();
        }

    }

}
