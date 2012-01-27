package org.jarbframework.populator;

import static org.jarbframework.utils.Asserts.notNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.populator.condition.ConditionalDatabaseUpdater;
import org.jarbframework.populator.condition.ResourceExists;
import org.jarbframework.utils.JdbcConnectionCallback;
import org.jarbframework.utils.JdbcUtils;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Update the database by executing an SQL resource.
 * @author Jeroen van Schagen
 * @since 01-06-2011
 */
public class SqlResourceDatabaseUpdater implements DatabaseUpdater {
    
    /** Data source being populated. **/
    private DataSource dataSource;
    /** SQL resource that should be executed. **/
    private Resource sqlResource;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setSqlResource(Resource sqlResource) {
        this.sqlResource = sqlResource;
    }
    
    /**
     * Construct a new {@link SqlResourceDatabaseUpdater} that skips
     * whenever the specified resource does not exist. Use this type of
     * database populator whenever it is uncertain if a resource exists.
     * 
     * @param sqlResource reference to the SQL script resource
     * @param dataSource JDBC data source that opens connections to our database
     * @return database populator that will execute SQL statements from the
     * specified script resource, whenever it exists
     */
    public static ConditionalDatabaseUpdater ignoreIfResourceMissing(Resource sqlResource, DataSource dataSource) {
        SqlResourceDatabaseUpdater updater = new SqlResourceDatabaseUpdater();
        updater.setSqlResource(sqlResource);
        updater.setDataSource(dataSource);
        return new ConditionalDatabaseUpdater(updater, new ResourceExists(sqlResource));
    }
    
    @Override
    public void update() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(notNull(sqlResource, "SQL resource cannot be null"));
        JdbcUtils.doWithConnection(dataSource, new JdbcConnectionCallback<Void>() {
            
            @Override
            public Void doWork(Connection connection) throws SQLException {
                populator.populate(connection);
                return null;
            }
            
        });
    }

    @Override
    public String toString() {
        return "SQL(" + sqlResource + ")";
    }
}
