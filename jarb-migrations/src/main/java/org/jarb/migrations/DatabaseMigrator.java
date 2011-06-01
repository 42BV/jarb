package org.jarb.migrations;

import java.sql.Connection;

/**
 * Capable of performing database migrations.
 * @author Jeroen van Schagen
 * @since 28-04-2011
 */
public interface DatabaseMigrator {

    /**
     * Perform a migration on some database connection. The provided
     * connection will automatically be closed post migration.
     * @param connection an open connection to the database.
     */
    void migrate(Connection connection);

}
