/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.init.migrate;

import java.util.HashSet;
import java.util.Set;

import org.jarbframework.utils.DataSourceDelegate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

/**
 * Embedded database builder that also migrates the schema.
 *
 * @author Jeroen van Schagen
 * @since Feb 12, 2014
 */
public class MigratingDatabaseBuilder extends EmbeddedDatabaseBuilder {
    
    private final Set<MigrationListener> listeners = new HashSet<MigrationListener>();

    private final DatabaseMigrator migrator;

    public MigratingDatabaseBuilder(DatabaseMigrator migrator) {
        this.migrator = migrator;
    }
    
    /**
     * Add a migration listener, invoked whenever the migration has occured.
     * 
     * @param listener the listener
     * @return this instance, for chaining
     */
    public MigratingDatabaseBuilder addListener(MigrationListener listener) {
        listeners.add(listener);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmbeddedDatabase build() {
        EmbeddedDatabase embeddedDatabase = super.build();

        MigratingDataSource migratingDataSource = new MigratingDataSource(embeddedDatabase, migrator);
        migratingDataSource.setListeners(listeners);

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
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void shutdown() {
            embeddedDatabase.shutdown();
        }
        
    }

}
