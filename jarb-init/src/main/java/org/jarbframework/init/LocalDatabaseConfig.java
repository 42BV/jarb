/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.init;

import javax.sql.DataSource;

import org.jarbframework.init.migrate.DatabaseMigrator;
import org.jarbframework.init.migrate.MigratingDatabaseBuilder;
import org.jarbframework.init.migrate.liquibase.LiquibaseMigrator;
import org.jarbframework.init.populate.PopulatingApplicationListener;
import org.jarbframework.init.populate.PopulatingApplicationListenerBuilder;
import org.jarbframework.init.populate.PopulatingApplicationListenerBuilder.DatabasePopulateAppender;
import org.springframework.context.annotation.Bean;

/**
 * Template for creating a local database configuration. 
 *
 * @author Jeroen van Schagen
 * @since Jun 11, 2015
 */
public abstract class LocalDatabaseConfig {
    
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

}
