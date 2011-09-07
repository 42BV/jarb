package org.jarbframework.utils;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public interface JdbcMetadataCallback<T> {
    
    T doWith(DatabaseMetaData databaseMetaData) throws SQLException;
    
}
