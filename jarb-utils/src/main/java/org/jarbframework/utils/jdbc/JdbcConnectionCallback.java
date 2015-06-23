package org.jarbframework.utils.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Callback interface invoked for the JDBC connection.
 *
 * @author Jeroen van Schagen
 * @date Sep 8, 2011
 *
 * @param <T> type of operation result
 */
public interface JdbcConnectionCallback<T> {

    /**
     * Perform operation(s) on a new JDBC connection.
     * @param connection the JDBC connection made available
     * @return result of the operation
     * @throws SQLException whenever an SQL error occurs
     */
    T doWork(Connection connection) throws SQLException;

}
