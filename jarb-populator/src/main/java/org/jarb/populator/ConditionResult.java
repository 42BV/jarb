package org.jarb.populator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConditionResult {
    private final List<String> failures;

    /**
     * Construct a new {@link ConditionResult}.
     */
    public ConditionResult() {
        failures = new ArrayList<String>();
    }

    /**
     * Create an empty (succesful) result.
     * @return sucesful result
     */
    public static ConditionResult success() {
        return new ConditionResult();
    }

    /**
     * Include a failure, causing {@link #isSatisfied()} to fail.
     * @param failure state failure that should be included
     * @return same result, for chaining
     */
    public ConditionResult addFailure(String failure, Object... arguments) {
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
    public ConditionResult verifyState(boolean state, String message, Object... arguments) {
        if(!state) { addFailure(message, arguments); }
        return this;
    }

    /**
     * Determine if this state is supported. Returns {@code true}
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