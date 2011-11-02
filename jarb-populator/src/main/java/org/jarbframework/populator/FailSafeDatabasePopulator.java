package org.jarbframework.populator;

import static org.jarbframework.utils.Asserts.notNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database populator that can skip exceptions from a delegate populator.
 * This populator can be used to prevent exceptions from being thrown.
 * 
 * @author Jeroen van Schagen
 * @since 17-06-2011
 */
public class FailSafeDatabasePopulator implements DatabasePopulator {
    private final Logger logger = LoggerFactory.getLogger(FailSafeDatabasePopulator.class);

    /** Populator that might throw an exception. **/
    private final DatabasePopulator populator;
    /** Classes of exceptions that should be skipped. **/
    private final Class<? extends Exception> exceptionClass;
    
    /** Determines if occured exceptions should be logged. **/
    private boolean shouldLogErrors = true;

    /**
     * Construct a new {@link FailSafeDatabasePopulator}.
     * @param populator the populator being wrapped
     */
    public FailSafeDatabasePopulator(DatabasePopulator populator) {
        this(populator, Exception.class);
    }

    /**
     * Construct a new {@link FailSafeDatabasePopulator}.
     * @param populator the populator being wrapped
     * @param exceptionClass exception class being skipped
     */
    public FailSafeDatabasePopulator(DatabasePopulator populator, Class<? extends Exception> exceptionClass) {
        this.populator = notNull(populator, "Populator cannot be null");
        this.exceptionClass = exceptionClass;
    }

    /**
     * Configure whether exceptions should be logged or not.
     * @param shouldLogErrors {@code true} if exceptions should be logged, else {@code false}
     */
    public void setShouldLogErrors(boolean shouldLogErrors) {
        this.shouldLogErrors = shouldLogErrors;
    }

    @Override
    public void populate() throws Exception {
        try {
            populator.populate();
        } catch (Exception exception) {
            if (isSkipableException(exception)) {
                logIfNeeded(exception);
            } else {
                throw exception;
            }
        }
    }

    /**
     * Determine if an exception is skipable. Whenever it is skipable,
     * we will prevent the exception from being thrown.
     * @param e exception that was initially thrown
     * @return {@code true} if it should be skipped, else {@code false}
     */
    private boolean isSkipableException(Exception exception) {
        return exceptionClass.isInstance(exception);
    }

    /**
     * Log the exception whenever desired.
     * @param e exception that should be logged
     */
    private void logIfNeeded(Exception e) {
        if (shouldLogErrors) {
            logger.info("An error occured while executing database populator (" + populator + ").", e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
