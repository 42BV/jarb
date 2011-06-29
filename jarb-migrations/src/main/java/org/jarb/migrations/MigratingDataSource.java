package org.jarb.migrations;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.jarb.utils.database.JdbcUtils;

/**
 * Data source that triggers database migrations during construction.
 * @author Jeroen van Schagen
 * @since 28-04-2011
 */
public class MigratingDataSource extends DataSourceDelegate {
    private DatabaseMigrator migrator;

    public void setMigrator(DatabaseMigrator migrator) {
        this.migrator = migrator;
    }

    /**
     * Perform our database migration.
     */
    @PostConstruct
    public void migrate() {
        Connection connection = null;
        try {
            connection = this.getConnection();
            migrator.migrate(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.closeQuietly(connection);
        }
    }

}
