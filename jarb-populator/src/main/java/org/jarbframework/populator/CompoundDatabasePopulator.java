package org.jarbframework.populator;

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
    private final Logger logger = LoggerFactory.getLogger(CompoundDatabasePopulator.class);

    /** Ordered collection of delegate populator. **/
    private final Iterable<DatabasePopulator> populators;
    /** Determine if we should continue after an exception. **/
    private boolean continueOnException = false;

    /**
     * Construct a new {@link CompoundDatabasePopulator}.
     * @param populators database populators that should be executed in sequence
     */
    public CompoundDatabasePopulator(Iterable<DatabasePopulator> populators) {
        this.populators = populators;
    }

    public void setContinueOnException(boolean continueOnException) {
        this.continueOnException = continueOnException;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate() throws Exception {
        for (DatabasePopulator populator : populators) {
            if (continueOnException) {
                populator = new FailSafeDatabasePopulator(populator);
            }
            logger.info("Starting populator '{}'...", populator);
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
