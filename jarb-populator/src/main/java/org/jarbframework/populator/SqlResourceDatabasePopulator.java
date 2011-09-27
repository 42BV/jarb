package org.jarbframework.populator;

import static org.jarbframework.utils.Asserts.notNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarbframework.populator.condition.ResourceExistsCondition;
import org.jarbframework.utils.JdbcConnectionCallback;
import org.jarbframework.utils.JdbcUtils;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Populate database using an SQL resource.
 * 
 * @author Jeroen van Schagen
 * @since 01-06-2011
 */
public class SqlResourceDatabasePopulator implements DatabasePopulator {
    /** Data source being populated. **/
    private DataSource dataSource;
    /** Reference to the SQL script resource. **/
    private Resource sqlResource;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setSqlResource(Resource sqlResource) {
        this.sqlResource = sqlResource;
    }
    
    /**
     * Construct a new {@link SqlResourceDatabasePopulator} that skips
     * whenever the specified resource does not exist. Use this type of
     * database populator whenever it is uncertain if a resource exists.
     * 
     * @param sqlResource reference to the SQL script resource
     * @param dataSource JDBC data source that opens connections to our database
     * @return database populator that will execute SQL statements from the
     * specified script resource, whenever it exists
     */
    public static ConditionalDatabasePopulator ignoreIfResourceMissing(Resource sqlResource, DataSource dataSource) {
        SqlResourceDatabasePopulator sqlPopulator = new SqlResourceDatabasePopulator();
        sqlPopulator.setSqlResource(sqlResource);
        sqlPopulator.setDataSource(dataSource);
        return new ConditionalDatabasePopulator(sqlPopulator, new ResourceExistsCondition(sqlResource));
    }
    
    @Override
    public void populate() throws Exception {
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("SQL populator '%s'", sqlResource);
    }

}
