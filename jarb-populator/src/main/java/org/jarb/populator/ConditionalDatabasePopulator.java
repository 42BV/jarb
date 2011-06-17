package org.jarb.populator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.util.StringUtils;

/**
 * Database populator that first checks its precondition before executing.
 * Whenever the precondition is not met, an illegal state exception is thrown.
 * 
 * @author Jeroen van Schagen
 * @since 17-06-2011
 */
public abstract class ConditionalDatabasePopulator implements DatabasePopulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionalDatabasePopulator.class);
    private boolean throwErrorOnFailure = false;

    public void setThrowErrorOnFailure(boolean throwErrorOnFailure) {
        this.throwErrorOnFailure = throwErrorOnFailure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void populate(Connection connection) throws SQLException {
        SupportsResult result = supports(connection);
        if(result.isSuccess()) {
            doPopulate(connection);
        } else {
            StringBuilder failureMessageBuilder = new StringBuilder();
            failureMessageBuilder.append("Database populator was skipped as precondition is not met:").append("\n - ");
            failureMessageBuilder.append(StringUtils.collectionToDelimitedString(result.getFailures(), "\n - "));
            final String failureMessage = failureMessageBuilder.toString();
            if(throwErrorOnFailure) {
                throw new IllegalStateException(failureMessage);
            } else {
                LOGGER.info(failureMessage);
            }
        }
    }
    
    /**
     * Perform the actual database population after our precondition was met.
     * @param connection the JDBC connection to use to populate the db; already configured and ready to use
     * @throws SQLException if an unrecoverable data access exception occurs during database population
     */
    protected abstract void doPopulate(Connection connection) throws SQLException;
    
    /**
     * Determine if this populator satisfies the requested precondition.
     * @param connection the JDBC connection to use to populate the db; already configured and ready to use
     * @return {@code true} if the precondition has been met, else {@code false}
     */
    protected abstract SupportsResult supports(Connection connection);
    
    /**
     * Result of {@link ConditionalDatabasePopulator#supports(Connection)}.
     */
    public static class SupportsResult {
        private final List<String> failures;
        
        public SupportsResult() {
            failures = new ArrayList<String>();
        }
        
        public static SupportsResult success() {
            return new SupportsResult();
        }
        
        public SupportsResult addFailure(String failure) {
            failures.add(failure);
            return this;
        }

        public boolean isSuccess() {
            return failures.isEmpty();
        }
        
        public List<String> getFailures() {
            return Collections.unmodifiableList(failures);
        }
    }
    
}
