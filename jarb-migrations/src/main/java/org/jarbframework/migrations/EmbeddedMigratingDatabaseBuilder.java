/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.migrations;

import org.jarbframework.migrations.liquibase.LiquibaseMigrator;
import org.jarbframework.utils.DataSourceDelegate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 * Embedded database builder that also migrates the schema.
 *
 * @author Jeroen van Schagen
 * @since Feb 12, 2014
 */
public class EmbeddedMigratingDatabaseBuilder extends EmbeddedDatabaseBuilder {
    
    private DatabaseMigrator migrator;
    
    /**
     * Configure a migrator.
     * @param migrator the migrator to set
     * @return this builder, for chaining
     */
    public EmbeddedMigratingDatabaseBuilder withMigrator(DatabaseMigrator migrator) {
        this.migrator = migrator;
        return this;
    }

    /**
     * Configure a local Liquibase migrator.
     * @return this builder, for chaining
     */
    public EmbeddedMigratingDatabaseBuilder withLiquibase() {
        return this.withMigrator(LiquibaseMigrator.local());
    }
    
    @Override
    public EmbeddedDatabase build() {
        if (migrator == null) {
            withLiquibase();
        }

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
    public class MigratingEmbeddedDatabase extends DataSourceDelegate implements EmbeddedDatabase {
        
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
