package org.jarbframework.utils.database;

import static org.jarbframework.utils.Conditions.notNull;
import static org.jarbframework.utils.JdbcUtils.doWithMetaData;
import static org.springframework.util.StringUtils.startsWithIgnoreCase;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.utils.JdbcMetadataCallback;

/**
 * Determines the database type based on JDBC meta data.
 *
 * @author Jeroen van Schagen
 * @date Sep 8, 2011
 */
public class JdbcMetadataDatabaseTypeResolver implements DatabaseTypeResolver {

    @Override
    public DatabaseType resolve(DataSource dataSource) {
        String databaseProductName = doWithMetaData(dataSource, new JdbcMetadataCallback<String>() {

            @Override
            public String doWith(DatabaseMetaData databaseMetaData) throws SQLException {
                return databaseMetaData.getDatabaseProductName();
            }

        });
        return notNull(lookupDatabaseType(databaseProductName), "Could not determine database type for '" + databaseProductName + "'");
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
