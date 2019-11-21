package nl._42.jarb.utils.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java Database Connectivity (JDBC) utility class.
 * @author Jeroen van Schagen
 * @since 08-05-2011
 */
public final class JdbcUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtils.class);

    /** Utility class, do not instantiate. */
    private JdbcUtils() {
    }

    /**
     * Open a data source connection and provide callback functionality on that connection.
     * After the callback has been invoked, the newly created connection will be closed.
     * The connection from the datasource will not explicitly be committed. (thus only works for read operations or when
     * datasource autocommit == true)
     *
     * @param <T> type of object returned by callback
     * @param dataSource - the data source that we should use
     * @param callback - the callback functionality being invoked
     * @return result of the callback, if any
     */
    public static <T> T doWithConnection(DataSource dataSource, Callback<T> callback) {
        return doWithConnection(dataSource, callback, false);
    }

    /**
     * Open a data source connection and provide callback functionality on that connection.
     * After the callback has been invoked, the newly created connection will be closed.
     * 
     * @param <T> type of object returned by callback
     * @param dataSource - the data source that we should use
     * @param commit - if datasource autocommit == false and this arg is true, the connection is explicitly committed after callback
     * @param callback - the callback functionality being invoked
     * @return result of the callback, if any
     */
    public static <T> T doWithConnection(DataSource dataSource, Callback<T> callback, boolean commit) {
        Connection connection = null;
        boolean shouldCommit = false;
        try {
            connection = dataSource.getConnection();
            shouldCommit = !connection.getAutoCommit() && commit;
            T result = callback.doWork(connection);
            if (shouldCommit) {
                connection.commit();
            }
            return result;
        } catch (SQLException e) {
            if (shouldCommit) {
                rollback(connection);
            }
            throw new RuntimeException(e);
        } finally {
            close(connection);
        }
    }

    private static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot close connection", e);
        }
    }

    private static void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                LOGGER.error("Cannot rollback transation", ex);
            }
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
