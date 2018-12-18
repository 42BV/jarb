/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.init.migrate.liquibase;

import static org.junit.Assert.assertEquals;

import liquibase.resource.ClassLoaderResourceAccessor;
import nl._42.jarb.migrate.liquibase.LiquibaseMigratingDatabaseBuilder;
import nl._42.jarb.migrate.liquibase.LiquibaseMigrator;

import nl._42.jarb.migrate.liquibase.LiquibaseMigratingDatabaseBuilder;
import nl._42.jarb.migrate.liquibase.LiquibaseMigrator;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Mar 13, 2014
 */
public class LiquibaseMigratingDatabaseBuilderTest {
    
    @Test
    public void testBuildEmbeddedDatabase() {
        EmbeddedDatabase database = new LiquibaseMigratingDatabaseBuilder(new LiquibaseMigrator(new ClassLoaderResourceAccessor()))
            .setChangeLogPath("changelog.xml")
            .setName("jarb-init-embedded")
            .setType(EmbeddedDatabaseType.HSQL)
                .build();
        
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(database);
            jdbcTemplate.execute("INSERT INTO persons (id) values (1)");
            assertEquals("henk", jdbcTemplate.queryForObject("SELECT name FROM persons WHERE id = 1", String.class));
        } finally {
            database.shutdown();
        }
    }

}
