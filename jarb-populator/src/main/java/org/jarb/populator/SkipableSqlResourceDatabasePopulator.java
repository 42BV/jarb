package org.jarb.populator;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.CannotReadScriptException;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Resource oriented database populator that skips population,
 * rather than throwing an exception, if one of its resources
 * cannot be accessed.
 * <p>
 * This database populator can be used to populate the database
 * with an optionally available resource.
 * 
 * @author Jeroen van Schagen
 * @since 01-06-2011
 */
public class SkipableSqlResourceDatabasePopulator implements DatabasePopulator {
    private final Resource script;

    /**
     * Construct a new {@link SkipableSqlResourceDatabasePopulator} for one resource.
     * @param script resource script that should be executed
     */
    public SkipableSqlResourceDatabasePopulator(Resource script) {
        this.script = script;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(Connection connection) throws SQLException {
        final ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(script);
        new FailSafeDatabasePopulator(resourceDatabasePopulator, CannotReadScriptException.class).populate(connection);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Skipable SQL %s", script);
    }

}
