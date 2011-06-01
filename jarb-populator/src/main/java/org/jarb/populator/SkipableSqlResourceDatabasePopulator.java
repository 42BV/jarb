package org.jarb.populator;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(SkipableSqlResourceDatabasePopulator.class);
    private final ResourceDatabasePopulator populator;

    /**
     * Construct a new {@link SkipableSqlResourceDatabasePopulator} for one resource.
     * @param script resource script that should be executed
     */
    public SkipableSqlResourceDatabasePopulator(Resource script) {
        this(new ResourceDatabasePopulator());
        populator.addScript(script);
    }

    /**
     * Construct a new {@link SkipableSqlResourceDatabasePopulator}.
     * @param populator resource based database populator being wrapped
     */
    public SkipableSqlResourceDatabasePopulator(ResourceDatabasePopulator populator) {
        this.populator = populator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(Connection connection) throws SQLException {
        try {
            populator.populate(connection);
        } catch (CannotReadScriptException e) {
            LOGGER.info("Skipped populating database as resource cannot be read.", e);
        }
    }

}
