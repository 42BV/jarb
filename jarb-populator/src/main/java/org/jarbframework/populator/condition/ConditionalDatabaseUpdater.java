package org.jarbframework.populator.condition;

import static org.apache.commons.lang3.StringUtils.join;

import org.jarbframework.populator.DatabaseUpdater;
import org.jarbframework.populator.DelegatingDatabaseUpdater;
import org.jarbframework.populator.condition.Condition.ConditionEvaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database updater that only executes whenever a desired condition is met.
 * @author Jeroen van Schagen
 * @since 17-06-2011
 */
public class ConditionalDatabaseUpdater extends DelegatingDatabaseUpdater {

    private final Logger logger = LoggerFactory.getLogger(ConditionalDatabaseUpdater.class);

    /** Delegate database updater. **/
	private final DatabaseUpdater delegate;
    /** Checks if the desired condition has been satisfied. **/
    private final Condition condition;
    
    /** Determine if an exception should be thrown if the condition is not satisfied. **/
    private boolean throwErrorIfUnsupported = false;

    /**
     * Construct a new {@link ConditionalDatabaseUpdater}.
     * @param delegate delegate database updater, invoked if the condition is satisfied
     * @param condition describes the condition that should be met to perform population
     */
    public ConditionalDatabaseUpdater(DatabaseUpdater delegate, Condition condition) {
        this.delegate = delegate;
        this.condition = condition;
    }

    @Override
    public void update() {
        ConditionEvaluation evaluation = condition.evaluate();
        if (evaluation.isSatisfied()) {
            super.update();
        } else {
            notifyConditionUnsatisfied(evaluation);
        }
    }

    private void notifyConditionUnsatisfied(ConditionEvaluation evaluation) {
        final String failureMessage = buildFailureMessage(evaluation);
        if (throwErrorIfUnsupported) {
            throw new IllegalStateException(failureMessage);
        } else {
            logger.info(failureMessage);
        }
    }

    private String buildFailureMessage(ConditionEvaluation evaluation) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Update (").append(getDelegate()).append(") was not performed, because:");
        messageBuilder.append("\n - ").append(join(evaluation.getFailures(), "\n - "));
        return messageBuilder.toString();
    }
    
    /**
     * Configure whether an exception should be thrown if the condition is not satisfied.
     * @param throwErrorIfUnsupported whether an exception should be thrown, or not
     */
    public void setThrowExceptionIfUnsupported(boolean throwErrorIfUnsupported) {
        this.throwErrorIfUnsupported = throwErrorIfUnsupported;
    }
    
    @Override
    protected DatabaseUpdater getDelegate() {
    	return delegate;
    }

    @Override
    public String toString() {
        return "Conditional(condition: " + condition + ", updater: " + getDelegate() + ")";
    }
}
