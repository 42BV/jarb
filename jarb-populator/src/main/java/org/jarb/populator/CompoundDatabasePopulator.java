package org.jarb.populator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database populator which can execute multiple populators in sequence.
 * 
 * @author Jeroen van Schagen
 * @since 01-06-2011
 */
public class CompoundDatabasePopulator implements DatabasePopulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompoundDatabasePopulator.class);

    /** Ordered collection of delegate populator. **/
    private final List<DatabasePopulator> populators;

    /** Determine if we should continue after an exception. **/
    private boolean continueOnException = false;

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
     * Include a database populator to this compound populator. Populators
     * will be executed in the same sequence as they were added.
     * @param populator database populator being added
     * @return this compound populator instance, useful for chaining
     */
    public CompoundDatabasePopulator add(DatabasePopulator populator) {
        populators.add(populator);
        return this;
    }

    public void setContinueOnException(boolean continueOnException) {
        this.continueOnException = continueOnException;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate() throws Exception {
        LOGGER.info("Starting to execute {} database populators.", populators.size());
        for (DatabasePopulator populator : populators) {
            LOGGER.info("Executing {}...", populator);
            if (continueOnException) {
                populator = new FailSafeDatabasePopulator(populator);
            }
            populator.populate();
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
