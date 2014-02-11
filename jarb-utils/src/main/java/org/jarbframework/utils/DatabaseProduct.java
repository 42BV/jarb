package org.jarbframework.utils;

import static org.jarbframework.utils.JdbcUtils.doWithConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Describes the product name and version of a specific database.
 * 
 * @author Jeroen van Schagen
 */
public final class DatabaseProduct {

    public final String databaseName;
    
    public final String version;

    public DatabaseProduct(String name, String version) {
        this.databaseName = name;
        this.version = version;
    }
    
    public String getName() {
        return databaseName;
    }
    
    public String getVersion() {
        return version;
    }
    
    /**
     * Determine the database product from a data source.
     * @param dataSource the data source
     * @return the database product
     */
    public static DatabaseProduct fromDataSource(DataSource dataSource) {
        return doWithConnection(dataSource, new JdbcConnectionCallback<DatabaseProduct>() {

            @Override
            public DatabaseProduct doWork(Connection connection) throws SQLException {
                DatabaseMetaData metaData = connection.getMetaData();
                String productName = metaData.getDatabaseProductName();
                String productVersion = metaData.getDatabaseProductVersion();
                return new DatabaseProduct(productName, productVersion);
            }

        });
    }
    
}
