package org.jarbframework.populator;

import static org.jarbframework.utils.Asserts.notNull;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.utils.JdbcConnectionCallback;
import org.jarbframework.utils.JdbcUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Update the database by executing an SQL resource.
 * 
 * @author Jeroen van Schagen
 * @since 01-06-2011
 */
public class SqlDatabasePopulator implements DatabasePopulator {

    /** Data source being populated. **/
    private final DataSource dataSource;
    
    /** Resource populator that should be executed. **/
    private final ResourceDatabasePopulator resourcePopulator;
    
    public SqlDatabasePopulator(DataSource dataSource, InputStream inputStream) {
        this(dataSource, new InputStreamResource(inputStream));
    }
    
    public SqlDatabasePopulator(DataSource dataSource, File file) {
        this(dataSource, new FileSystemResource(file));
    }

    public SqlDatabasePopulator(DataSource dataSource, Resource resource) {
        this.dataSource = notNull(dataSource, "Data source cannot be null.");
        
        resourcePopulator = new ResourceDatabasePopulator();
        resourcePopulator.addScript(notNull(resource, "Resource cannot be null."));
    }

    public static SqlDatabasePopulator fromSql(DataSource dataSource, String sql) {
        return new SqlDatabasePopulator(dataSource, new ByteArrayResource(sql.getBytes()));
    }

    @Override
    public void populate() {
        JdbcUtils.doWithConnection(dataSource, new JdbcConnectionCallback<Void>() {

            @Override
            public Void doWork(Connection connection) throws SQLException {
                resourcePopulator.populate(connection);
                return null;
            }

        });
    }

}
