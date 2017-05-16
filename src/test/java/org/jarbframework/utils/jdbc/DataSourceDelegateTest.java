package org.jarbframework.utils.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.utils.jdbc.DataSourceDelegate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Simple test that ensures all method calls are delegated to our underlying data source.
 * 
 * @author Jeroen van Schagen
 * @since 02-05-2011
 */
public class DataSourceDelegateTest {
    
    private DataSource dataSourceMock;
    
    private DataSourceDelegate delegatingDataSource;

    @Before
    public void setUp() {
        dataSourceMock = Mockito.mock(DataSource.class);
        delegatingDataSource = new DataSourceDelegate(dataSourceMock);
    }

    @Test
    public void testGetConnection() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(dataSourceMock.getConnection()).thenReturn(connection);
        Connection result = delegatingDataSource.getConnection();
        assertEquals(connection, result);
    }

    @Test
    public void testGetConnectionByParameters() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(dataSourceMock.getConnection("jeroen", "password")).thenReturn(connection);
        Connection result = delegatingDataSource.getConnection("jeroen", "password");
        assertEquals(connection, result);
    }

    @Test
    public void testSetLoginTimeout() throws SQLException {
        final int loginTimeout = 42;
        delegatingDataSource.setLoginTimeout(loginTimeout);
        Mockito.verify(dataSourceMock).setLoginTimeout(loginTimeout);
    }

    @Test
    public void testGetLoginTimeout() throws SQLException {
        final int loginTimeout = 42;
        Mockito.when(dataSourceMock.getLoginTimeout()).thenReturn(loginTimeout);
        assertEquals(loginTimeout, delegatingDataSource.getLoginTimeout());
    }

    @Test
    public void testSetLogWriter() throws SQLException {
        final PrintWriter out = new PrintWriter(new CharArrayWriter());
        delegatingDataSource.setLogWriter(out);
        Mockito.verify(dataSourceMock).setLogWriter(out);
    }

    @Test
    public void testGetLogWriter() throws SQLException {
        final PrintWriter out = new PrintWriter(new CharArrayWriter());
        Mockito.when(dataSourceMock.getLogWriter()).thenReturn(out);
        assertEquals(out, delegatingDataSource.getLogWriter());
    }

    @Test
    public void testIsWrapperFor() throws SQLException {
    	Mockito.when(dataSourceMock.isWrapperFor(String.class)).thenReturn(true);
        assertTrue(delegatingDataSource.isWrapperFor(String.class));
    }

    @Test
    public void testUnwrap() throws SQLException {
        final String value = "test";
        Mockito.when(dataSourceMock.unwrap(String.class)).thenReturn(value);
        assertEquals(value, delegatingDataSource.unwrap(String.class));
    }

}
