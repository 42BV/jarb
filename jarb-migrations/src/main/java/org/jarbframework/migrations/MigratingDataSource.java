package org.jarbframework.migrations;

import static org.jarbframework.utils.JdbcUtils.closeQuietly;
import static org.jarbframework.utils.JdbcUtils.commitSafely;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.utils.Asserts;
import org.jarbframework.utils.DataSourceDelegate;

/**
 * Data source that triggers database migrations during construction.
 * @author Jeroen van Schagen
 * @since 28-04-2011
 */
public class MigratingDataSource extends DataSourceDelegate {
    
    /** Performs the actual database migration on a JDBC connection. **/
    private final DatabaseMigrator migrator;

    /** Migration username, whenever left empty we use the data source username. **/
    private String username;
    
    /** Migration password, only used whenever the username property is not blank. **/
    private String password;

    private boolean migrated = false;
    
    public MigratingDataSource(DataSource delegate, DatabaseMigrator migrator) {
        super(delegate);
        this.migrator = Asserts.notNull(migrator, "Migrator cannot be null");
    }

    @Override
    public Connection getConnection() throws SQLException {
        migrateOnDemand();
        return super.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        migrateOnDemand();
        return super.getConnection(username, password);
    }

    /**
     * Run the database migration, whenever it hasn't been executed yet.
     */
    private void migrateOnDemand() throws SQLException {
        if (shouldMigrate()) {
            doMigrate();
            migrated = true;
        }
    }

    private boolean shouldMigrate() {
        return !migrated;
    }

    private void doMigrate() throws SQLException {
        Connection connection = openMigrationConnection();
        migrator.migrate(connection);
        commitSafely(connection);
        closeQuietly(connection);
    }

    private Connection openMigrationConnection() throws SQLException {
        return (username == null || username.isEmpty()) ? super.getConnection() : super.getConnection(username, password);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
