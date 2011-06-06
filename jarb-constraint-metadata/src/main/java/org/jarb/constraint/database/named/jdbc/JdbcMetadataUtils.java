package org.jarb.constraint.database.named.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class JdbcMetadataUtils {

    /**
     * Retrieve all table names inside the database.
     * @param metadata database metadata
     * @return collection of all table names
     * @throws SQLException whenever an exception occurs while querying
     */
    public static Set<String> getTableNames(DatabaseMetaData metadata) throws SQLException {
        Set<String> tableNames = new HashSet<String>();
        ResultSet resultSet = metadata.getTables(null, null, null, null);
        while (resultSet.next()) {
            tableNames.add(resultSet.getString("TABLE_NAME"));
        }
        return tableNames;
    }

}
