package org.jarb.migrations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Simple test that ensures all method calls are
 * provided to our delegate data source.
 * 
 * @author Jeroen van Schagen
 * @since 02-05-2011
 */
public class DataSourceDelegateTest {
    private DataSource dataSourceMock;
    private DataSourceDelegate delegatingDataSource;

    @Before
    public void setUp() {
        dataSourceMock = EasyMock.createMock(DataSource.class);
        delegatingDataSource = new DataSourceDelegate();
        delegatingDataSource.setDelegate(dataSourceMock);
    }

    @Test
    public void testGetConnection() throws SQLException {
        Connection connection = EasyMock.createMock(Connection.class);
        EasyMock.expect(dataSourceMock.getConnection()).andReturn(connection);
        EasyMock.replay(dataSourceMock);
        Connection result = delegatingDataSource.getConnection();
        EasyMock.verify(dataSourceMock);
        assertEquals(connection, result);
    }

    @Test
    public void testGetConnectionByParameters() throws SQLException {
        Connection connection = EasyMock.createMock(Connection.class);
        EasyMock.expect(dataSourceMock.getConnection("jeroen", "password")).andReturn(connection);
        EasyMock.replay(dataSourceMock);
        Connection result = delegatingDataSource.getConnection("jeroen", "password");
        EasyMock.verify(dataSourceMock);
        assertEquals(connection, result);
    }

    @Test
    public void testSetLoginTimeout() throws SQLException {
        final int loginTimeout = 42;
        dataSourceMock.setLoginTimeout(loginTimeout);
        EasyMock.expectLastCall();
        EasyMock.replay(dataSourceMock);
        delegatingDataSource.setLoginTimeout(loginTimeout);
        EasyMock.verify(dataSourceMock);
    }

    @Test
    public void testGetLoginTimeout() throws SQLException {
        final int loginTimeout = 42;
        EasyMock.expect(dataSourceMock.getLoginTimeout()).andReturn(loginTimeout);
        EasyMock.replay(dataSourceMock);
        assertEquals(loginTimeout, delegatingDataSource.getLoginTimeout());
        EasyMock.verify(dataSourceMock);
    }

    @Test
    public void testSetLogWriter() throws SQLException {
        final PrintWriter out = new PrintWriter(new CharArrayWriter());
        dataSourceMock.setLogWriter(out);
        EasyMock.expectLastCall();
        EasyMock.replay(dataSourceMock);
        delegatingDataSource.setLogWriter(out);
        EasyMock.verify(dataSourceMock);
    }

    @Test
    public void testGetLogWriter() throws SQLException {
        final PrintWriter out = new PrintWriter(new CharArrayWriter());
        EasyMock.expect(dataSourceMock.getLogWriter()).andReturn(out);
        EasyMock.replay(dataSourceMock);
        assertEquals(out, delegatingDataSource.getLogWriter());
        EasyMock.verify(dataSourceMock);
    }

    @Test
    public void testIsWrapperFor() throws SQLException {
        EasyMock.expect(dataSourceMock.isWrapperFor(String.class)).andReturn(true);
        EasyMock.replay(dataSourceMock);
        assertTrue(delegatingDataSource.isWrapperFor(String.class));
        EasyMock.verify(dataSourceMock);
    }

    @Test
    public void testUnwrap() throws SQLException {
        final String value = "test";
        EasyMock.expect(dataSourceMock.unwrap(String.class)).andReturn(value);
        EasyMock.replay(dataSourceMock);
        assertEquals(value, delegatingDataSource.unwrap(String.class));
        EasyMock.verify(dataSourceMock);
    }

}
