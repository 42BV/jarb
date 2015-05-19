/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.init.migrate.liquibase;

import org.jarbframework.init.migrate.MigratingDatabaseBuilder;

/**
 * Embedded database builder that also migrates the schema.
 *
 * @author Jeroen van Schagen
 * @since Feb 12, 2014
 */
public class LiquibaseMigratingDatabaseBuilder extends MigratingDatabaseBuilder {
    
    /**
     * The liquibase migrator to be used.
     */
    private final LiquibaseMigrator migrator;
    
    /**
     * Create database from the default liquibase migrator.
     */
    public LiquibaseMigratingDatabaseBuilder() {
        this(LiquibaseMigrator.local());
    }
    
    /**
     * Create database from a specific liquibase migrator.
     * @param migrator the liquibase migrator
     */
    public LiquibaseMigratingDatabaseBuilder(LiquibaseMigrator migrator) {
        super(migrator);
        this.migrator = migrator;
    }

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

}
