package org.jarbframework.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class AutoCommitDataSourceWrapperTest {
    
    @Mock
    private DataSource dataSource;
    
    @Mock
    private Connection connection;
    
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
