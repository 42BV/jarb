package org.jarbframework.utils;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Callback interface invoked for the database meta data.
 *
 * @author Jeroen van Schagen
 * @date Sep 8, 2011
 *
 * @param <T> type of operation result
 */
public interface JdbcMetadataCallback<T> {

    /**
     * Perform an operation using the given database meta data.
     * @param databaseMetaData the database meta data
     * @return result of the operation
     * @throws SQLException whenever an SQL error occurs
     */
    T doWith(DatabaseMetaData databaseMetaData) throws SQLException;

}
