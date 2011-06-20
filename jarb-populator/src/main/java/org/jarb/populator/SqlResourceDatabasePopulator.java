package org.jarb.populator;

import java.sql.Connection;
import java.sql.SQLException;

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
    private Resource sqlResource;
    
    public void setSqlResource(Resource sqlResource) {
        this.sqlResource = sqlResource;
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
