/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import org.jarbframework.migrate.DatabaseMigrator;
import org.jarbframework.migrate.MigratingDatabaseBuilder;
import org.jarbframework.migrate.liquibase.LiquibaseMigrator;
import org.jarbframework.populate.DatabasePopulator;
import org.jarbframework.populate.PopulatingApplicationListener;
import org.jarbframework.populate.PopulatingApplicationListenerBuilder;
import org.jarbframework.populate.SqlDatabasePopulator;
import org.jarbframework.populate.SqlDirectoryDatabasePopulator;
import org.jarbframework.populate.PopulatingApplicationListenerBuilder.DatabasePopulateAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Template for creating a local database configuration. 
 *
 * @author Jeroen van Schagen
 * @since Jun 11, 2015
 */
public abstract class LocalDatabaseConfig {
    
    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Create our local (embedded) data source.
     * 
     * @return the created data source
     */
    @Bean
    public DataSource dataSource() {
        DatabaseMigrator migrator = migrator();
        MigratingDatabaseBuilder builder = new MigratingDatabaseBuilder(migrator);
        configure(builder);
        return builder.build();
    }
    
    /**
     * Configure the database before it is created.
     * 
     * @param builder the database builder
     */
    protected void configure(MigratingDatabaseBuilder builder) {
    }
    
    /**
     * Create the migrator that will initialize our database schema.
     * 
     * @return the migrator
     */
    protected DatabaseMigrator migrator() {
        return LiquibaseMigrator.local();
    }

    /**
     * Create the application listener that will populate the database
     * after the schema has been successfully set up.
     * 
     * @return the application listener
     */
    @Bean
    public PopulatingApplicationListener dataPopulator() {
        PopulatingApplicationListenerBuilder builder = new PopulatingApplicationListenerBuilder();
        
        DatabasePopulateAppender initializer = builder.initializer();
        initializer(initializer);
        initializer.done();
        
        DatabasePopulateAppender destroyer = builder.destroyer();
        destroyer(destroyer);
        destroyer.done();

        return builder.build();
    }

    /**
     * Configure all populators that should be used to fill the database.
     * 
     * @param initializer the populator appender
     */
    protected void initializer(DatabasePopulateAppender initializer) {
    }
    
    /**
     * Config all populators that should be used to clean the database.
     * 
     * @param destroyer the populator appender
     */
    protected void destroyer(DatabasePopulateAppender destroyer) {
    }
    
    /**
     * Create a new SQL based database populator. You can provide either
     * a single file or a directory. When a directory is provided we will
     * sequentially load all .sql files in that directory, except for
     * scripts dependant on another database product e.g. 
     * <code>001_import@psql.sql</code> when on HSQLDB.
     * 
     * @param location the location of SQL file or directory
     * @return the database populator
     */
    protected DatabasePopulator sql(String location) {
        Resource resource = resourceLoader.getResource(location);
        try {
            File file = resource.getFile();
            if (file.isDirectory()) {
                return new SqlDirectoryDatabasePopulator(dataSource(), file);
            } else {
                return new SqlDatabasePopulator(dataSource(), resource);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Could not create SQL database populator for: " + location, ioe);
        }
    }

}
