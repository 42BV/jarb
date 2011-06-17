package org.jarb.populator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.util.Assert;

/**
 * Database populator that can skip exceptions from a delegate populator.
 * This populator can be used to prevent exceptions from being thrown
 * during database population.
 * 
 * @author Jeroen van Schagen
 * @since 17-06-2011
 */
public class FailSafeDatabasePopulator implements DatabasePopulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FailSafeDatabasePopulator.class);
    
    /** Populator being wrapped with skip logic. **/
    private final DatabasePopulator populator;
    
    /** Classes of exceptions that should be skipped. **/
    private Set<Class<? extends Exception>> skippingExceptionClasses;
    /** Determines if occured exceptions should be logged. **/
    private boolean logError = true;
    
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
     * @param skippingExceptionClass exception class being skipped
     */
    public FailSafeDatabasePopulator(DatabasePopulator populator, Class<? extends Exception> skippingExceptionClass) {
        Assert.notNull(populator, "Delegate populator cannot be null");
        this.populator = populator;
        skippingExceptionClasses = new HashSet<Class<? extends Exception>>();
        skippingExceptionClasses.add(skippingExceptionClass);
    }
    
    /**
     * Configure the exception classes that should be skipped.
     * @param skippingExceptionClasses exception classes being skipped
     */
    public void setSkippingExceptionClasses(Set<Class<? extends Exception>> skippingExceptionClasses) {
        this.skippingExceptionClasses = skippingExceptionClasses;
    }
    
    /**
     * Configure whether exceptions should be logged or not.
     * @param logError {@code true} if exceptions should be logged, else {@code false}
     */
    public void setLogError(boolean logError) {
        this.logError = logError;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(Connection connection) throws SQLException {
        try {
            populator.populate(connection);
        } catch(SQLException e) {
          if(isSkipableException(e)) {
                logIfNeeded(e);
            } else {
                throw e;
            }
        } catch(RuntimeException e) {
            if(isSkipableException(e)) {
                logIfNeeded(e);
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Determine if an exception is skipable. Whenever it is skipable,
     * we will prevent the exception from being thrown.
     * @param e exception that was initially thrown
     * @return {@code true} if it should be skipped, else {@code false}
     */
    private boolean isSkipableException(Exception e) {
        for(Class<? extends Exception> skippingExceptionClass : skippingExceptionClasses) {
            if(skippingExceptionClass.isAssignableFrom(e.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Log the exception whenever desired.
     * @param e exception that should be logged
     */
    private void logIfNeeded(Exception e) {
        if(logError) {
            LOGGER.warn("An error occured while executing database populator (" + populator + ").", e);
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
