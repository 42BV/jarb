package org.jarb.migrations.liquibase;

import java.sql.Connection;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;

/**
 * Capable of building liquibase instances.
 * @author Jeroen van Schagen
 * @since 4-5-2011
 */
public interface LiquibaseFactory {

    /**
     * Build a new liquibase instance, based on a connection.
     * @param changeLogPath path to the change log
     * @param connection the connection to use
     * @return new liquibase instance
     * @throws LiquibaseException whenever an exception occurs during creation
     */
    Liquibase build(String changeLogPath, Connection connection) throws LiquibaseException;

}
