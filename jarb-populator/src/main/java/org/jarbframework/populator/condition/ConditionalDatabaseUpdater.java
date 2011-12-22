package org.jarbframework.populator.condition;

import static org.apache.commons.lang3.StringUtils.join;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jarbframework.populator.DatabaseUpdater;
import org.jarbframework.populator.WrappingDatabaseUpdater;
import org.jarbframework.populator.condition.Condition.ConditionEvaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database updater that only executes whenever a desired condition is met.
 * @author Jeroen van Schagen
 * @since 17-06-2011
 */
public class ConditionalDatabaseUpdater extends WrappingDatabaseUpdater {
    private final Logger logger = LoggerFactory.getLogger(ConditionalDatabaseUpdater.class);

    /** Checks if the desired condition has been satisfied **/
    private final Condition conditional;
    /** Determine if an exception should be thrown if the condition is not satisfied. **/
    private boolean throwErrorIfUnsupported = false;

    /**
     * Construct a new {@link ConditionalDatabaseUpdater}.
     * @param populator delegate database populator, invoked if the condition is satisfied
     * @param conditional describes the condition that should be met to perform population
     */
    public ConditionalDatabaseUpdater(DatabaseUpdater populator, Condition conditional) {
        super(populator);
        this.conditional = conditional;
    }

    /**
     * Configure whether an exception should be thrown if the condition is not satisfied.
     * @param throwErrorIfUnsupported {@code true} if an exception should be thrown,
     * or {@link false} if it should only be logged
     * @return this populator, for method chaining
     */
    public ConditionalDatabaseUpdater setThrowExceptionIfUnsupported(boolean throwErrorIfUnsupported) {
        this.throwErrorIfUnsupported = throwErrorIfUnsupported;
        return this;
    }

    @Override
    public void update() throws Exception {
        ConditionEvaluation evaluation = conditional.evaluate();
        if (evaluation.isSatisfied()) {
            super.update();
        } else {
            notifyConditionUnsatisfied(evaluation);
        }
    }

    private void notifyConditionUnsatisfied(ConditionEvaluation evaluation) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Update (").append(getDelegate()).append(") was not performed, because:");
        messageBuilder.append("\n - ").append(join(evaluation.getFailures(), "\n - "));
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
