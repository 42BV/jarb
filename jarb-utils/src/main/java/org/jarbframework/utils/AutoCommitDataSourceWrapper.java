package org.jarbframework.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DelegatingDataSource;

/**
 * Utility class that wraps any Datasource but on getting the connection sets the AutoCommit property to true.
 *
 * @author thijs
 */
public class AutoCommitDataSourceWrapper extends DelegatingDataSource {

    private final boolean autoCommit;

    public AutoCommitDataSourceWrapper(DataSource dataSource, boolean autoCommit) {
        super(dataSource);
        this.autoCommit = autoCommit;
    }

    @Override
    @SuppressWarnings("PMD.CloseResource")
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        conn.setAutoCommit(autoCommit);
        return conn;
    }

    @Override
    @SuppressWarnings("PMD.CloseResource")
    public Connection getConnection(String username, String password) throws SQLException {
        Connection conn = super.getConnection(username, password);
        conn.setAutoCommit(autoCommit);
        return conn;
    }

}
