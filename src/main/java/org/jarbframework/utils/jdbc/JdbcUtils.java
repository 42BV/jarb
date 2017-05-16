package org.jarbframework.utils.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.utils.Classes;

/**
 * Java Database Connectivity (JDBC) utility class.
 * @author Jeroen van Schagen
 * @since 08-05-2011
 */
public final class JdbcUtils {

    /** Utility class, do not instantiate. */
    private JdbcUtils() {
    }
    
    public static <T> T doWithConnection(String driverClassName, String url, String userName, String password, JdbcConnectionCallback<T> callback) {
        Connection connection = null;
        try {
            connection = createConnection(driverClassName, url, userName, password);
            return callback.doWork(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(connection);   
        }
    }

    private static Connection createConnection(String driverClassName, String url, String userName, String password) throws SQLException {
        Classes.forName(driverClassName); // Register appropriate JDBC driver
        return DriverManager.getConnection(url, userName, password);
    }

    /**
     * Open a data source connection and provide callback functionality on that connection.
     * After the callback has been invoked, the newly created connection will be closed.
     * 
     * @param <T> type of object returned by callback
     * @param dataSource the data source that we should use
     * @param callback the callback functionality being invoked
     * @return result of the callback, if any
     */
    public static <T> T doWithConnection(DataSource dataSource, JdbcConnectionCallback<T> callback) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return callback.doWork(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(connection);
        }
    }

    /**
     * Close the connection, whenever it isn't {@code null}, and wrap any SQL exceptions into a runtime exception.
     * 
     * @param connection our connection to close
     */
    public static void closeQuietly(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Commits the connection whenver auto commit has been disabled.
     * 
     * @param connection our connection to commit
     */
    public static void commitSafely(Connection connection) {
        try {
            if (! connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
