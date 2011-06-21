package org.jarb.populator;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jarb.populator.condition.ResourceExistsConditionChecker;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.Assert;

/**
 * Populate database using an SQL resource.
 * 
 * @author Jeroen van Schagen
 * @since 01-06-2011
 */
public class SqlResourceDatabasePopulator extends JdbcDatabasePopulator {
    /** Reference to the SQL script resource. **/
    private Resource sqlResource;
    
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
        return new ConditionalDatabasePopulator(sqlPopulator, new ResourceExistsConditionChecker(sqlResource));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void populateInConnection(final Connection connection) throws SQLException {
        Assert.state(sqlResource != null, "SQL script resource cannot be null");
        
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(sqlResource);
        populator.populate(connection);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("SQL populator '%s'", sqlResource);
    }

}
