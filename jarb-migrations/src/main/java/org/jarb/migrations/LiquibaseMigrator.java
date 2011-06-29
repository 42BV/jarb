package org.jarb.migrations;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Liquibase specific implementation of {@link DatabaseMigrator}.
 * @author Jeroen van Schagen
 * @since 28-04-2011
 */
public class LiquibaseMigrator implements DatabaseMigrator {
    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseMigrator.class);

    // Required liquibase information
    private String changeLogPath = "src/main/db/changelog.groovy";
    private ResourceAccessor resourceAccessor = new FileSystemResourceAccessor();
    
    // Optional migration details
    private boolean dropFirst = false;
    private Map<String, String> parameters;
    private String defaultSchemaName;
    private String contexts = "";
    private int changesToApply = 0;
    
    /** Configure this property whenever an output file should be created. **/
    private String outputFilePath;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void migrate(Connection connection) {
        try {
            final Liquibase liquibase = createLiquibase(connection);
            if (dropFirst) {
                liquibase.dropAll();
            }
            if (shouldWriteSqlOutput()) {
                writeSqlOutput(liquibase);
            }
            migrateDatabase(liquibase);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Liquibase createLiquibase(Connection connection) throws LiquibaseException {
        Liquibase liquibase = new Liquibase(changeLogPath, resourceAccessor, connectionToDatabase(connection));
        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                liquibase.setChangeLogParameter(entry.getKey(), entry.getValue());
            }
        }
        return liquibase;
    }

    /**
     * Wrap our connection inside a liquibase component, which handles database connectivity.
     * @param connection the connection to our database
     * @return new database instance, for our connection
     * @throws DatabaseException whenever the database instance could not be created
     */
    private Database connectionToDatabase(Connection connection) throws DatabaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        if (defaultSchemaName != null) {
            database.setDefaultSchemaName(defaultSchemaName);
        }
        return database;
    }

    /**
     * Perform migration changes to database.
     * @param liquibase provides migration functionality
     * @throws LiquibaseException whenever an error occurs during migration
     */
    private void migrateDatabase(Liquibase liquibase) throws LiquibaseException {
        if (changesToApply > 0) {
            liquibase.update(changesToApply, contexts);
        } else {
            liquibase.update(contexts);
        }
    }
    
    /**
     * Determine if a SQL output should be written away.
     * @return {@code true} if it should, else {@code false}
     */
    private boolean shouldWriteSqlOutput() {
        return isNotBlank(outputFilePath);
    }

    /**
     * Convert the changes to SQL, and write them away.
     * @param liquibase provides migration functionality
     */
    private void writeSqlOutput(Liquibase liquibase) {
        Writer writer;
        try {
            writer = newSqlOutputWriter();
        } catch (IOException e) {
            LOGGER.error("Could not construct a writer for our generated change-set SQL.", e);
            return; // Continue running, SQL file generation is not critical
        }
        LOGGER.info("Writing the generated change-set SQL to '{}'...", outputFilePath);
        try {
            doWriteSqlOutput(liquibase, writer);
        } catch (LiquibaseException e) {
            LOGGER.error("Could not write out the generated change-set SQL.", e);
            return; // Continue running, SQL file generation is not critical
        }
    }

    /**
     * Construct a writer for our generated SQL files.
     * @return new writer that can write SQL files
     * @throws IOException whenever an IO based exception occurs
     */
    protected Writer newSqlOutputWriter() throws IOException {
        return new FileWriter(outputFilePath, true);
    }

    /**
     * Perform the actual writing of our generated SQL.
     * @param liquibase used to convert our change sets into SQL
     * @param writer writes the generated SQL into our output
     * @throws LiquibaseException whenever liquibase encouters a critical problem
     */
    private void doWriteSqlOutput(Liquibase liquibase, Writer writer) throws LiquibaseException {
        if (changesToApply > 0) {
            liquibase.update(changesToApply, contexts, writer);
        } else {
            liquibase.update(contexts, writer);
        }
    }

    public void setChangeLogPath(String changeLogPath) {
        this.changeLogPath = changeLogPath;
    }
    
    public void setResourceAccessor(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }

    public void setDropFirst(boolean dropFirst) {
        this.dropFirst = dropFirst;
    }
    
    public void setDefaultSchemaName(String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
    }
    
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    public void setContexts(String contexts) {
        this.contexts = contexts;
    }

    public void setChangesToApply(int changesToApply) {
        this.changesToApply = changesToApply;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

}
