package org.jarbframework.populator;

import static org.springframework.util.StringUtils.collectionToDelimitedString;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jarbframework.populator.condition.ConditionCheckResult;
import org.jarbframework.populator.condition.ConditionChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database populator that only executes whenever a desired condition is met.
 * 
 * @author Jeroen van Schagen
 * @since 17-06-2011
 */
public class ConditionalDatabasePopulator implements DatabasePopulator {
    private final Logger logger = LoggerFactory.getLogger(ConditionalDatabasePopulator.class);

    /** Delegate database populator, invoked if condition is satisfied. **/
    private final DatabasePopulator populator;
    /** Checks if the desired condition has been satisfied **/
    private final ConditionChecker conditionChecker;
    /** Determine if an exception should be thrown if the condition is not satisfied. **/
    private boolean throwErrorIfUnsupported = false;

    /**
     * Construct a new {@link ConditionalDatabasePopulator}.
     * @param populator delegate database populator, invoked if condition is satisfied
     * @param conditionChecker checks if the desired condition is met
     */
    public ConditionalDatabasePopulator(DatabasePopulator populator, ConditionChecker conditionChecker) {
        this.populator = populator;
        this.conditionChecker = conditionChecker;
    }

    /**
     * Configure whether an exception should be thrown if the condition is not satisfied.
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
        ConditionCheckResult conditionCheck = conditionChecker.checkCondition();
        if (conditionCheck.isSatisfied()) {
            populator.populate();
        } else {
            StringBuilder failureMessageBuilder = new StringBuilder();
            failureMessageBuilder.append("Database populator (").append(populator).append(") was not executed, because:");
            failureMessageBuilder.append("\n - ").append(collectionToDelimitedString(conditionCheck.getFailures(), "\n - "));
            final String failureMessage = failureMessageBuilder.toString();
            if (throwErrorIfUnsupported) {
                throw new IllegalStateException(failureMessage);
            } else {
                logger.info(failureMessage);
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
