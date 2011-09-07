package org.jarbframework.utils.database;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class JdbcMetadataDriverResolverTest {

    @Test
    public void testResolveMetaData() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:jarb");
        dataSource.setUsername("sa");
        
        JdbcMetadataDatabaseTypeResolver databaseTypeResolver = new JdbcMetadataDatabaseTypeResolver();
        assertEquals(DatabaseType.HSQL, databaseTypeResolver.resolve(dataSource));
    }
    
}
