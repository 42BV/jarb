package org.jarbframework.populator;

import static org.apache.commons.lang3.StringUtils.join;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jarbframework.populator.Conditional.Condition;
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
    private final Conditional conditional;
    
    /** Determine if an exception should be thrown if the condition is not satisfied. **/
    private boolean throwErrorIfUnsupported = false;

    /**
     * Construct a new {@link ConditionalDatabasePopulator}.
     * @param populator delegate database populator, invoked if the condition is satisfied
     * @param conditional describes the condition that should be met to perform population
     */
    public ConditionalDatabasePopulator(DatabasePopulator populator, Conditional conditional) {
        this.populator = populator;
        this.conditional = conditional;
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

    @Override
    public void populate() throws Exception {
        Condition condition = conditional.check();
        if (condition.isSatisfied()) {
            populator.populate();
        } else {
            notifyPopulatorNotPerformed(condition);
        }
    }

    private void notifyPopulatorNotPerformed(Condition condition) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Database populator (").append(populator).append(") was not performed, because:");
        messageBuilder.append("\n - ").append(join(condition.getFailures(), "\n - "));
        final String failureMessage = messageBuilder.toString();
        if (throwErrorIfUnsupported) {
            throw new IllegalStateException(failureMessage);
        } else {
            logger.info(failureMessage);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
