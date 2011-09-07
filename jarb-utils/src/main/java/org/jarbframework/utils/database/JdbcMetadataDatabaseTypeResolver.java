package org.jarbframework.utils.database;

import static org.jarbframework.utils.JdbcUtils.doWithMetaData;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.jarbframework.utils.JdbcMetadataCallback;

public class JdbcMetadataDatabaseTypeResolver implements DatabaseTypeResolver {
    private static final Map<String, DatabaseType> PRODUCT_NAMES = new HashMap<String, DatabaseType>();
    
    static {
        PRODUCT_NAMES.put("HSQL Database Engine", DatabaseType.HSQL);
        PRODUCT_NAMES.put("MySQL Database Engine", DatabaseType.MYSQL);
        PRODUCT_NAMES.put("Oracle Database Engine", DatabaseType.ORACLE);
        PRODUCT_NAMES.put("PostgreSQL Database Engine", DatabaseType.POSTGRESQL);
    }

    @Override
    public DatabaseType resolve(DataSource dataSource) {
        String databaseProductName = doWithMetaData(dataSource, new JdbcMetadataCallback<String>() {
            
            @Override
            public String doWith(DatabaseMetaData databaseMetaData) throws SQLException {
                return databaseMetaData.getDatabaseProductName();
            }
            
        });
        return lookupDatabaseType(databaseProductName);
    }

    private DatabaseType lookupDatabaseType(String databaseProductName) {
        return PRODUCT_NAMES.get(databaseProductName);
    }

}
