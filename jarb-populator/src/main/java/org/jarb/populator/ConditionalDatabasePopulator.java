package org.jarb.populator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    /** Determine if an exception should be thrown if {@link #supports()} fails. **/
    private boolean throwErrorIfUnsupported = false;

    /**
     * Configure whether an exception should be thrown if {@link #supports()} fails.
     * @param throwErrorIfUnsupported {@code true} if an exception should be thrown,
     * or {@link false} if it should only be logged
     * @return this populator, for method chaining
     */
    public ConditionalDatabasePopulator setThrowExceptionIfUnsupported(boolean throwErrorIfUnsupported) {
        this.throwErrorIfUnsupported = throwErrorIfUnsupported;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void populate() throws Exception {
        SupportsResult result = supports();
        if (result.isSuccess()) {
            doPopulate();
        } else {
            StringBuilder failureMessageBuilder = new StringBuilder();
            failureMessageBuilder.append("Database populator (").append(this).append(") was not executed, because:");
            failureMessageBuilder.append("\n - ").append(StringUtils.collectionToDelimitedString(result.getFailures(), "\n - "));
            final String failureMessage = failureMessageBuilder.toString();
            if (throwErrorIfUnsupported) {
                throw new IllegalStateException(failureMessage);
            } else {
                LOGGER.info(failureMessage);
            }
        }
    }

    /**
     * Perform the actual database population after our precondition was met.
     * @throws Exception if an unrecoverable exception occurs during database population
     */
    protected abstract void doPopulate() throws Exception;

    /**
     * Determine if this populator satisfies the requested precondition.
     * @return {@code true} if the precondition has been met, else {@code false}
     */
    protected abstract SupportsResult supports();

    /**
     * Result of {@link ConditionalDatabasePopulator#supports()}.
     */
    public static class SupportsResult {
        private final List<String> failures;

        /**
         * Construct a new {@link SupportsResult}.
         */
        public SupportsResult() {
            failures = new ArrayList<String>();
        }

        /**
         * Create an empty (succesful) result.
         * @return sucesful result
         */
        public static SupportsResult success() {
            return new SupportsResult();
        }

        /**
         * Include a failure, causing {@link #isSuccess()} to fail.
         * @param failure state failure that should be included
         * @return same result, for chaining
         */
        public SupportsResult addFailure(String failure, Object... arguments) {
            failures.add(String.format(failure, arguments));
            return this;
        }
        
        /**
         * Whenever the specified state is {@code false}, include
         * the specified failure message.
         * @param state the state that should evaluate {@code true}
         * @param message failure message if state is {@code false}
         * @return same result, for chaining
         */
        public SupportsResult verifyState(boolean state, String message, Object... arguments) {
            if(!state) { addFailure(message, arguments); }
            return this;
        }

        /**
         * Determine if this state is supported. Returns {@code true}
         * whenever there are no failure messages.
         * @return {@code true} if succesful, else {@code false}
         */
        public boolean isSuccess() {
            return failures.isEmpty();
        }

        /**
         * Retrieve an unmodifiable list of failure messages.
         * @return failure messages
         */
        public List<String> getFailures() {
            return Collections.unmodifiableList(failures);
        }
    }

}
