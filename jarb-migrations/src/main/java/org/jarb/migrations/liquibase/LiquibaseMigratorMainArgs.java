package org.jarb.migrations.liquibase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jarb.migrations.DatabaseMigrator;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Command line arguments for {@link LiquibaseMigratorMain}.
 * 
 * @author BVos
 */
@Parameters(separators = "= ")
public class LiquibaseMigratorMainArgs {

    @Parameter(names = "-driverClass", required = true, description = "Fully qualified name of the jdbc driver class")
    private String driverClassName;

    @Parameter(names = "-dbUrl", required = true, description = "Database url")
    private String dbUrl;

    @Parameter(names = "-dbUser", required = true, description = "Database username")
    private String dbUserName;

    @Parameter(names = "-dbPassword", required = true, description = "Database password")
    private String dbPassword;

    @Parameter(names = "-changelogPath", required = true, description = "Liquibase changelog path")
    private String changelogPath;

    @Parameter(names = "-sqlLogPath", required = false, description = "Sql log filepath (optional)")
    private String sqlLogPath;

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getChangelogPath() {
        return changelogPath;
    }

    public String getSqlLogPath() {
        return sqlLogPath;
    }

    /**
     * Factory method that creates a DatabaseMigrator based upon given arguments.
     * @return new DatabaseMigrator instance, used to migrate the database
     */
    public DatabaseMigrator createMigrator() {
        LiquibaseMigrator migrator = new LiquibaseMigrator();
        migrator.setChangeLogPath(getChangelogPath());
        migrator.setOutputFilePath(getSqlLogPath());
        return migrator;
    }

    /**
     * Factory method that creates a JDBC connection based upon given arguments.
     * @return connection to the database
     */
    public Connection createConnection() {
        Connection conn = null;
        try {
            // Register appropriate JDBC driver
            Class.forName(getDriverClassName());
            conn = DriverManager.getConnection(getDbUrl(), getDbUserName(), getDbPassword());
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe);
        }
        return conn;
    }
}
