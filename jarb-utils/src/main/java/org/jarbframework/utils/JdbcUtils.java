package org.jarbframework.utils;

import static org.jarbframework.utils.Conditions.notNull;
import static org.springframework.jdbc.datasource.DataSourceUtils.getConnection;
import static org.springframework.jdbc.datasource.DataSourceUtils.releaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Java Database Connectivity (JDBC) utility class.
 * @author Jeroen van Schagen
 * @since 08-05-2011
 */
public final class JdbcUtils {

    /**
     * Provides callback functionality on the meta data of a data source.
     * Before the callback gets invoked, a connection is acquired, and
     * after execution the connection is released.
     * @param <T> type of object returned by callback
     * @param dataSource the data source that we should use
     * @param callback the callback functionality being invoked
     * @return result of the callback, if any
     */
    public static <T> T doWithMetaData(DataSource dataSource, JdbcMetadataCallback<T> callback) {
        Connection connection = notNull(getConnection(dataSource), "Could not open connection");
        try {
            return callback.doWith(connection.getMetaData());
        } catch (SQLException e) {
            throw new RuntimeException("Could not open connection to access meta data.", e);
        } finally {
            releaseConnection(connection, dataSource);
        }
    }

    /**
     * Close the connection, whenever it isn't {@code null}, and
     * wrap any SQL exceptions into a runtime exception.
     * @param connection our connection to close
     */
    public static void closeQuietly(Connection connection) {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void commitSafely(Connection connection) {
        try {
            if (!connection.getAutoCommit()) connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
