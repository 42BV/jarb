package org.jarbframework.utils.jdbc;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.util.ReflectionUtils;

/**
 * Delegate implementation of {@link DataSource}. Delegates all method invocation to
 * a delegate data source instance.
 * 
 * @author Jeroen van Schagen
 * @since 28-04-2011
 */
public class DataSourceDelegate implements DataSource {
    
    /**
     * Underlying data source to which we delegate operations.
     */
    private final DataSource delegate;

    public DataSourceDelegate(DataSource delegate) {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return delegate.getConnection(username, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegate.getLogWriter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return delegate.getLoginTimeout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        delegate.setLogWriter(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        delegate.setLoginTimeout(seconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(Class<?> type) throws SQLException {
        return delegate.isWrapperFor(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(Class<T> type) throws SQLException {
        return delegate.unwrap(type);
    }

    /**
     * New method added in Java 7, this method is invoked with
     * reflection to provide backwards compatibility with Java 6.
     * 
     * @return the parent logger
     * @throws SQLFeatureNotSupportedException whenever the feature is not supported
     */
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        Method method = ReflectionUtils.findMethod(delegate.getClass(), "getParentLogger");
        return (Logger) ReflectionUtils.invokeMethod(method, delegate);
    }

}
