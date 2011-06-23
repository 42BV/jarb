package org.jarb.populator.condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of {@link ConditionChecker#checkCondition()}. The
 * condition has been accepted whenever {@link #isSatisfied()}
 * evaluates into {@code true}. If the condition is not satisfied,
 * look at {@link #getFailures()}.
 * 
 * @author Jeroen van Schagen
 * @since 21-06-2011
 */
public class ConditionCheckResult {
    private final List<String> failures;

    /**
     * Construct a new {@link ConditionCheckResult}.
     */
    public ConditionCheckResult() {
        failures = new ArrayList<String>();
    }

    /**
     * Create an empty (succesful) result.
     * @return sucesful result
     */
    public static ConditionCheckResult success() {
        return new ConditionCheckResult();
    }
    
    /**
     * Whenever the specified state is {@code false}, include
     * the specified failure message.
     * @param state the state that should evaluate {@code true}
     * @param message failure message if state is {@code false}
     * @return same result, for chaining
     */
    public ConditionCheckResult checkState(boolean state, String message) {
        if(!state) { addFailure(message); }
        return this;
    }

    /**
     * Include a failure, causing {@link #isSatisfied()} to fail.
     * @param failure state failure that should be included
     * @return same result, for chaining
     */
    public ConditionCheckResult addFailure(String failure) {
        failures.add(failure);
        return this;
    }

    /**
     * Determine if this condition is supported. Returns {@code true}
     * whenever there are no failure messages.
     * @return {@code true} if succesful, else {@code false}
     */
    public boolean isSatisfied() {
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