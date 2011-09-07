package org.jarbframework.migrations.liquibase;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import org.jarbframework.migrations.DatabaseMigrator;
import org.jarbframework.utils.JdbcUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

/**
 * This class provides command-line support for Liquibase migrations.
 * 
 * @author Bas de Vos
 * @author Jeroen van Schagen
 * 
 * @since 5 September 2011
 */
public final class LiquibaseMigratorMain {

    /**
     * Invokes the Liquibase migrations provided.
     * @param args all migration arguments
     */
    public static void main(String[] args) {
        LiquibaseMigrationCommand migrationCommand = new LiquibaseMigrationCommand();
        JCommander argumentParser = new JCommander(migrationCommand);
        try {
            argumentParser.parse(args);
            migrationCommand.execute();
        } catch (ParameterException pe) {
            pe.printStackTrace();
            argumentParser.usage();
        }
    }

    @Parameters(separators = "= ")
    public static final class LiquibaseMigrationCommand {

        @Parameter(names = "-driverClass", required = true, description = "Fully qualified name of the JDBC driver class")
        private String driverClassName;

        @Parameter(names = "-dbUrl", required = true, description = "Database URL")
        private String dbUrl;

        @Parameter(names = "-dbUser", required = true, description = "Database username")
        private String dbUserName;

        @Parameter(names = "-dbPassword", required = true, description = "Database password")
        private String dbPassword;

        @Parameter(names = "-changeLogBaseDir", description = "Liquibase changelog base dir (optional)")
        private String changeLogBaseDir;

        @Parameter(names = "-changeLogPath", required = true, description = "Liquibase changelog path")
        private String changeLogPath;

        @Parameter(names = "-sqlOutputPath", description = "SQL output file path (optional)")
        private String sqlOutputPath;

        @Parameter(names = "-dropFirst", description = "Drop first (optional)")
        private boolean dropFirst = false;

        /**
         * Perform Liquibase migration on the database.
         */
        public void execute() {
            Connection connection = null;
            try {
                connection = createConnection();
                createMigrator().migrate(connection);
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                JdbcUtils.closeQuietly(connection);
            }
        }

        /**
         * Factory method that creates a {@link DatabaseMigrator} based upon given arguments.
         * @return new DatabaseMigrator, capable of performing database migrations
         */
        private DatabaseMigrator createMigrator() {
            LiquibaseMigrator migrator = new LiquibaseMigrator();
            migrator.setChangeLogPath(changeLogPath);
            migrator.setResourceAccessor(createResourceAccessor());
            migrator.setOutputFilePath(sqlOutputPath);
            migrator.setDropFirst(dropFirst);
            return migrator;
        }

        private ResourceAccessor createResourceAccessor() {
            FileSystemResourceAccessor accessor;
            if (isBlank(changeLogBaseDir)) {
                accessor = new FileSystemResourceAccessor();
            } else {
                accessor = new FileSystemResourceAccessor(changeLogBaseDir);
            }
            return accessor;
        }

        /**
         * Factory method that creates a JDBC connection based upon given arguments.
         * @return connection to the database
         */
        private Connection createConnection() {
            try {
                Class.forName(driverClassName); // Register appropriate JDBC driver
                return DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            } catch (SQLException sqle) {
                throw new RuntimeException(sqle);
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException(cnfe);
            }
        }

    }

}
