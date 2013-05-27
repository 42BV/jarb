package org.jarbframework.populator;

import static org.jarbframework.utils.Asserts.notNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.utils.JdbcConnectionCallback;
import org.jarbframework.utils.JdbcUtils;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Update the database by executing an SQL resource.
 * @author Jeroen van Schagen
 * @since 01-06-2011
 */
public class SqlResourceDatabasePopulator implements DatabasePopulator {

    /** Data source being populated. **/
    private final DataSource dataSource;
    
    /** Resource that should be executed. **/
    private final Resource resource;

    public SqlResourceDatabasePopulator(DataSource dataSource, Resource resource) {
        this.dataSource = dataSource;
        this.resource = resource;
    }
    
    @Override
    public void populate() {
        final ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(notNull(resource, "Resource cannot be null."));
        JdbcUtils.doWithConnection(dataSource, new JdbcConnectionCallback<Void>() {

            @Override
            public Void doWork(Connection connection) throws SQLException {
                resourceDatabasePopulator.populate(connection);
                return null;
            }

        });
    }

}
