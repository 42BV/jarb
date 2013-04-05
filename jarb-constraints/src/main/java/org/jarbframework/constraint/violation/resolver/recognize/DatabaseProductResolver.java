package org.jarbframework.constraint.violation.resolver.recognize;

import static org.jarbframework.utils.JdbcUtils.doWithConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.utils.JdbcConnectionCallback;

public class DatabaseProductResolver {

    public DatabaseProduct getDatabaseProduct(DataSource dataSource) {
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
