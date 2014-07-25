package org.jarbframework.migrations.liquibase;

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

import org.jarbframework.migrations.DatabaseMigrator;

/**
 * Liquibase specific implementation of {@link DatabaseMigrator}.
 * @author Jeroen van Schagen
 * @since 28-04-2011
 */
public class LiquibaseMigrator implements DatabaseMigrator {
    
    private final ResourceAccessor resourceAccessor;
    
    private String changeLogPath = "changelog.groovy";

    private boolean dropFirst = false;

    private Map<String, String> parameters;
    
    private String defaultSchemaName;
    
    private String contexts = "";
    
    private int changesToApply = 0;

    private String outputFilePath;

    /**
     * Construct a new {@link LiquibaseMigrator} that runs from the current working directory.
     */
    public LiquibaseMigrator() {
        this(new FileSystemResourceAccessor());
    }

    /**
     * Construct a new {@link LiquibaseMigrator} from a specific base path.
     * @param basePath the base path to run from
     */
    public LiquibaseMigrator(String basePath) {
        this(new FileSystemResourceAccessor(basePath));
    }

    /**
     * Construct a new {@link LiquibaseMigrator} with a specific resource accessor.
     * @param resourceAccessor the resource accessor that should be applied
     */
    public LiquibaseMigrator(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }
    
    /**
     * Construct a new {@link LiquibaseMigrator} linked to the src/main/db/changelog.groovy.
     * @return the created liquibase migrator
     */
    public static LiquibaseMigrator local() {
        LiquibaseMigrator migrator = new LiquibaseMigrator();
        migrator.setChangeLogPath("src/main/db/changelog.groovy");
        return migrator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void migrate(Connection connection) {
        try {
            final Liquibase liquibase = buildLiquibase(connection);
            liquibase.getLog().info("Starting liquibase migration.");

            if (dropFirst) {
                liquibase.dropAll();
            }
            if (shouldGenerateSqlScript()) {
                liquibase.getLog().info("The database migration will occur twice, once to generate an SQL script and afterwards to do the actual migration.");
                generateSqlScript(liquibase);
            }
            migrateDatabase(liquibase);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Liquibase buildLiquibase(Connection connection) throws LiquibaseException {
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
    private boolean shouldGenerateSqlScript() {
        return outputFilePath != null && ! outputFilePath.isEmpty();
    }

    /**
     * Convert the changes to SQL, and write them away.
     * @param liquibase provides migration functionality
     * @throws LiquibaseException 
     * @throws IOException 
     */
    private void generateSqlScript(Liquibase liquibase) throws LiquibaseException, IOException {
        Writer writer = new FileWriter(outputFilePath, true);
        try {
            if (changesToApply > 0) {
                liquibase.update(changesToApply, contexts, writer);
            } else {
                liquibase.update(contexts, writer);
            }
        } finally {
            writer.close();
        }
    }

    public void setChangeLogPath(String changeLogPath) {
        this.changeLogPath = changeLogPath;
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
