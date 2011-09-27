package org.jarbframework.populator.condition;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Result type from {@link Condition#check()}. The condition has been
 * met whenever {@link #isSatisfied()} evaluates into {@code true}. If
 * the condition is not satisfied, look at {@link #getFailures()}.
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
     * Include a failure, causing {@link #isSatisfied()} to fail.
     * @param failure state failure that should be included
     * @return same result, for chaining
     */
    public ConditionCheckResult fail(String failure) {
        failures.add(failure);
        return this;
    }

    /**
     * Determine if the checked condition is satisfied.
     * @return {@code true} if successful, else {@code false}
     */
    public boolean isSatisfied() {
        return failures.isEmpty();
    }

    /**
     * Failure messages that describe why the condition was not satisfied.
     * @return failure messages
     */
    public List<String> getFailures() {
        return unmodifiableList(failures);
    }

}