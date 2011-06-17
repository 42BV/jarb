package org.jarb.populator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.init.DatabasePopulator;

/**
 * Database populator which can execute multiple populators in sequence.
 * 
 * @author Jeroen van Schagen
 * @since 01-06-2011
 */
public class CompoundDatabasePopulator implements DatabasePopulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompoundDatabasePopulator.class);
    private final List<DatabasePopulator> populators;

    /**
     * Construct an empty {@link CompoundDatabasePopulator}.
     */
    public CompoundDatabasePopulator() {
        this.populators = new ArrayList<DatabasePopulator>();
    }

    /**
     * Construct a new {@link CompoundDatabasePopulator}.
     * @param populators database populators that should be executed in sequence
     */
    public CompoundDatabasePopulator(Collection<DatabasePopulator> populators) {
        this.populators = new ArrayList<DatabasePopulator>(populators);
    }

    /**
     * Append a database populator to this compound populator.
     * @param populator database populator being added
     * @return this compound populator instance, useful for chaining
     */
    public CompoundDatabasePopulator add(DatabasePopulator populator) {
        populators.add(populator);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(Connection connection) throws SQLException {
        LOGGER.info("Starting to execute {} database populators.", populators.size());
        for (DatabasePopulator populator : populators) {
            LOGGER.info("Executing {}...", populator);
            populator.populate(connection);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
