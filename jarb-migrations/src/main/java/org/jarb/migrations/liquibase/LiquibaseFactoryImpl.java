package org.jarb.migrations.liquibase;

import java.sql.Connection;
import java.util.Map;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

/**
 * Default implementation of {@link LiquibaseFactory}.
 * @author Jeroen van Schagen
 * @since 04-05-2011
 */
public class LiquibaseFactoryImpl implements LiquibaseFactory {
    private ResourceAccessor resourceAccessor = new FileSystemResourceAccessor();
    private Map<String, String> parameters;
    private String defaultSchemaName;

    /**
     * {@inheritDoc}
     */
    @Override
    public Liquibase build(String changeLogPath, Connection connection) throws LiquibaseException {
        Liquibase liquibase = new Liquibase(changeLogPath, resourceAccessor, connectionToDatabase(connection));
        return withChangeLogParameters(liquibase);
    }

    /**
     * Wrap our connection inside a liquibase component, which handles database connectivity.
     * @param connection the connection to our database
     * @return new database instance, for our connection
     * @throws DatabaseException whenever the database instance could not be created
     */
    protected Database connectionToDatabase(Connection connection) throws DatabaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        if (defaultSchemaName != null) {
            database.setDefaultSchemaName(defaultSchemaName);
        }
        return database;
    }

    /**
     * Store any change log parameters in our liquibase instance.
     * @param liquibase the liquibase being enriched with parameters
     * @return same liquibase instance, only with parameters
     */
    private Liquibase withChangeLogParameters(Liquibase liquibase) {
        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                liquibase.setChangeLogParameter(entry.getKey(), entry.getValue());
            }
        }
        return liquibase;
    }

    public void setResourceAccessor(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void setDefaultSchemaName(String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
    }

}
