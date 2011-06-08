package org.jarb.migrations.liquibase;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;

import org.jarb.migrations.DatabaseMigrator;
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
    private LiquibaseFactory liquibaseFactory = new LiquibaseFactoryImpl();
    private String changeLogPath = "src/main/db/changelog.groovy";

    // Optional migration details
    private boolean dropFirst = false;
    private int changesToApply = 0;
    private String contexts = "";
    private String outputFilePath;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void migrate(Connection connection) {
        try {
            final Liquibase liquibase = liquibaseFactory.build(changeLogPath, connection);
            if (dropFirst) {
                liquibase.dropAll();
            }
            if (isNotBlank(outputFilePath)) {
                writeSqlOutput(liquibase);
            }
            migrateDatabase(liquibase);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
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

    public void setLiquibaseFactory(LiquibaseFactory liquibaseFactory) {
        this.liquibaseFactory = liquibaseFactory;
    }

    public void setDropFirst(boolean dropFirst) {
        this.dropFirst = dropFirst;
    }

    public void setChangesToApply(int changesToApply) {
        this.changesToApply = changesToApply;
    }

    public void setContexts(String contexts) {
        this.contexts = contexts;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

}
