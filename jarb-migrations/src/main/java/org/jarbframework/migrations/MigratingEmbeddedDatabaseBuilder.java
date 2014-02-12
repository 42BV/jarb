/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.migrations;

import org.jarbframework.migrations.liquibase.LiquibaseMigrator;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 * Embedded database builder that also migrates the schema.
 *
 * @author Jeroen van Schagen
 * @since Feb 12, 2014
 */
public class MigratingEmbeddedDatabaseBuilder extends EmbeddedDatabaseBuilder {
    
    private DatabaseMigrator databaseMigrator;
    
    public MigratingEmbeddedDatabaseBuilder withMigrator(DatabaseMigrator databaseMigrator) {
        this.databaseMigrator = databaseMigrator;
        return this;
    }

    public MigratingEmbeddedDatabaseBuilder withLiquibase() {
        return this.withMigrator(new LiquibaseMigrator("src/main/db"));
    }
    
    @Override
    public EmbeddedDatabase build() {
        if (databaseMigrator == null) {
            withLiquibase();
        }

        EmbeddedDatabase embeddedDatabase = super.build();
        
        MigratingDataSource migratingDataSource = new MigratingDataSource(embeddedDatabase);
        migratingDataSource.setMigrator(databaseMigrator);
        return new MigratingEmbeddedDatabase(migratingDataSource, embeddedDatabase);

    }

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
