package nl._42.jarb.utils.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Java Database Connectivity (JDBC) utility class.
 * @author Jeroen van Schagen
 * @since 08-05-2011
 */
public final class JdbcUtils {

    /** Utility class, do not instantiate. */
    private JdbcUtils() {
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
    public static <T> T doWithConnection(DataSource dataSource, Callback<T> callback) {
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

    private static void closeQuietly(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JDBC connection callback
     * @param <T> the result type
     */
    @FunctionalInterface
    public interface Callback<T> {

        /**
         * Work with active JDBC connection.
         * @param connection the connection
         * @return the result
         * @throws SQLException whenever something goes wrong
         */
        T doWork(Connection connection) throws SQLException;

    }

}
