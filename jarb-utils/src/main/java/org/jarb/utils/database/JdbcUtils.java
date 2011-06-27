package org.jarb.utils.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Java Database Connectivity (JDBC) utility class.
 * 
 * @author Jeroen van Schagen
 * @since 08-05-2011
 */
public final class JdbcUtils {

    /**
     * Commit the connection whenever it does not autocommit.
     * @param connection our connection to commit
     */
    public static void commitSafely(Connection connection) {
        try {
            if (!connection.getAutoCommit())
                connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Close the connection, whenever it isn't {@code null}, and
     * wrap any SQL exceptions into a runtime exception.
     * @param connection our connection to close
     */
    public static void closeQuietly(Connection connection) {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Close the statement, whenever it isn't {@code null}, and
     * wrap any SQL exceptions into a runtime exception.
     * @param statement our statement to close
     */
    public static void closeQuietly(Statement statement) {
        try {
            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private JdbcUtils() {
        // Prevent initialization
    }

}
