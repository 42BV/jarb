package org.jarb.migrations.liquibase;

import java.sql.Connection;
import java.sql.SQLException;

import org.jarb.migrations.DatabaseMigrator;
import org.jarb.utils.JdbcUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * This class provides command-line support for Liquibase migrations.
 * 
 * @author BVos
 */
public class LiquibaseMigratorMain {

    /**
     * Invokes the Liquibase migrations provided.
     * @param args all migration arguments
     */
    public static void main(String[] args) throws SQLException {
        LiquibaseMigratorMainArgs params = new LiquibaseMigratorMainArgs();
        JCommander cmd = new JCommander(params);
        Connection conn = null;
        try {
            cmd.parse(args);
            conn = params.createConnection();
            DatabaseMigrator migrator = params.createMigrator();
            migrator.migrate(conn);
            conn.commit();
        } catch (ParameterException pe) {
            pe.printStackTrace();
            cmd.usage();
        } finally {
            JdbcUtils.closeQuietly(conn);
        }
    }

}
