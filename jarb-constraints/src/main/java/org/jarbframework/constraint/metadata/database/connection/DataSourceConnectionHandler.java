package org.jarbframework.constraint.metadata.database.connection;

import javax.sql.DataSource;

import org.jarbframework.utils.Asserts;
import org.jarbframework.utils.JdbcConnectionCallback;
import org.jarbframework.utils.JdbcUtils;

public class DataSourceConnectionHandler implements ConnectionHandler {

    private final DataSource dataSource;
    
    public DataSourceConnectionHandler(DataSource dataSource) {
        this.dataSource = Asserts.notNull(dataSource, "Data source cannot be null.");
    }
    
    @Override
    public <T> T execute(JdbcConnectionCallback<T> callback) {
        return JdbcUtils.doWithConnection(dataSource, callback);
    }
    
}
