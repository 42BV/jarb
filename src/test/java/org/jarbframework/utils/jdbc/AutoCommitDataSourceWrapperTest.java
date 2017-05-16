package org.jarbframework.utils.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.utils.jdbc.AutoCommitDataSourceWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AutoCommitDataSourceWrapperTest {
    
    private DataSource dataSource;
    
    private Connection connection;
    
    @Before
    public void setUp() {
        dataSource = Mockito.mock(DataSource.class);
        connection = Mockito.mock(Connection.class);
    }

    @Test
    public void testGetConnection() throws SQLException {
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        AutoCommitDataSourceWrapper wrapper = new AutoCommitDataSourceWrapper(dataSource, true);
        wrapper.getConnection();
        
        Mockito.verify(connection).setAutoCommit(true);
    }
    
    @Test
    public void testGetConnectionUsrPwd() throws SQLException {
        Mockito.when(dataSource.getConnection(Mockito.anyString(), Mockito.anyString())).thenReturn(connection);

        AutoCommitDataSourceWrapper wrapper = new AutoCommitDataSourceWrapper(dataSource, false);
        wrapper.getConnection("test","test");
        
        Mockito.verify(connection).setAutoCommit(false);
    }

}
