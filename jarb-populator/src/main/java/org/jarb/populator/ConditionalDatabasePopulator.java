package org.jarb.populator;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jarb.populator.condition.ConditionChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Database populator that only executes whenever a desired condition is met.
 * 
 * @author Jeroen van Schagen
 * @since 17-06-2011
 */
public class ConditionalDatabasePopulator implements DatabasePopulator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionalDatabasePopulator.class);
    /** Delgate database populator, invoked if condition is satisfied. **/
    private final DatabasePopulator populator;
    /** Used to check if a desired condition is met. **/
    private final ConditionChecker conditionChecker;
    /** Determine if an exception should be thrown if the condition is not satisfied. **/
    private boolean throwErrorIfUnsupported = false;
    
    /**
     * Construct a new {@link ConditionalDatabasePopulator}.
     * @param populator delgate database populator, invoked if condition is satisfied
     * @param conditionChecker checks if the desired condition is met
     */
    public ConditionalDatabasePopulator(DatabasePopulator populator, ConditionChecker conditionChecker) {
        this.populator = populator;
        this.conditionChecker = conditionChecker;
    }

    /**
     * Configure whether an exception should be thrown if {@link #shouldExecute()} fails.
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
    public void populate() throws Exception {
        ConditionResult result = conditionChecker.checkCondition();
        if (result.isSatisfied()) {
            populator.populate();
        } else {
            StringBuilder failureMessageBuilder = new StringBuilder();
            failureMessageBuilder.append("Database populator (").append(populator).append(") was not executed, because:");
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
