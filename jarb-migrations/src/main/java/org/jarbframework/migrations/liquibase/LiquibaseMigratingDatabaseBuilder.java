/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.migrations.liquibase;

import org.jarbframework.migrations.MigratingDataSource;
import org.jarbframework.utils.DataSourceDelegate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 * Embedded database builder that also migrates the schema.
 *
 * @author Jeroen van Schagen
 * @since Feb 12, 2014
 */
public class LiquibaseMigratingDatabaseBuilder extends EmbeddedDatabaseBuilder {
    
    private LiquibaseMigrator migrator = LiquibaseMigrator.local();

    /**
     * Change the change log path, this is by default 'src/main/db/changelog.groovy'
     * 
     * @param changeLogPath the change log path
     * @return this builder instance, for chaining
     */
    public LiquibaseMigratingDatabaseBuilder setChangeLogPath(String changeLogPath) {
        migrator.setChangeLogPath(changeLogPath);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmbeddedDatabase build() {
        EmbeddedDatabase embeddedDatabase = super.build();
        MigratingDataSource migratingDataSource = new MigratingDataSource(embeddedDatabase, migrator);
        return new MigratingEmbeddedDatabase(migratingDataSource, embeddedDatabase);
    }

    /**
     * Adapts the migrating data source to our embedded database interface.
     *
     * @author Jeroen van Schagen
     * @since Mar 4, 2014
     */
    public static class MigratingEmbeddedDatabase extends DataSourceDelegate implements EmbeddedDatabase {
        
        private final EmbeddedDatabase embeddedDatabase;
        
        private MigratingEmbeddedDatabase(MigratingDataSource migratingDataSource, EmbeddedDatabase embeddedDatabase) {
            super(migratingDataSource);
            this.embeddedDatabase = embeddedDatabase;
        }
        
        @Override
        public void shutdown() {
            embeddedDatabase.shutdown();
        }
        
    }

}
