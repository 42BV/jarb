package org.jarbframework.utils.database;

import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.jarbframework.utils.JdbcUtils.doWithConnection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.utils.JdbcConnectionCallback;

/**
 * Determines the database type using our JDBC meta data.
 * @author Jeroen van Schagen
 * @since Sep 8, 2011
 */
public class JdbcMetadataDatabaseTypeResolver implements DatabaseTypeResolver {

    @Override
    public DatabaseType resolve(DataSource dataSource) {
        String databaseProductName = doWithConnection(dataSource, new JdbcConnectionCallback<String>() {

            @Override
            public String doWork(Connection connection) throws SQLException {
                return connection.getMetaData().getDatabaseProductName();
            }

        });
        DatabaseType databaseType = lookupDatabaseType(databaseProductName);
        if (databaseType == null) {
            throw new UnrecognizedDatabaseException("Could not determine database type for '" + databaseProductName + "'");
        }
        return databaseType;
    }

    /**
     * Resolve the database type for a specific product name.
     * @param databaseProductName name of the database product
     * @return matching database type, if it could be resolved
     */
    private DatabaseType lookupDatabaseType(String databaseProductName) {
        DatabaseType databaseType = null;
        for (DatabaseType supportedType : DatabaseType.values()) {
            if (startsWithIgnoreCase(databaseProductName, supportedType.name())) {
                databaseType = supportedType;
                break;
            }
        }
        return databaseType;
    }

}
