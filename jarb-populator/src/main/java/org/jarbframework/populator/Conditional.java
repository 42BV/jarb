package org.jarbframework.populator;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a conditional that can be checked on our current state.
 */
public interface Conditional {
    
    /**
     * Determine if a conditional check is satisfied.
     * @return conditional check result, see {@link Condition#isSatisfied()}
     */
    Condition check();
    
    /**
     * Result type from {@link Conditional#check()}. The condition has been
     * met whenever {@link #isSatisfied()} evaluates into {@code true}. If
     * the condition is not satisfied, look at {@link #getFailures()}.
     * 
     * @author Jeroen van Schagen
     * @since 21-06-2011
     */
    public class Condition {
        private final List<String> failures;

        /**
         * Construct a new {@link Condition}.
         */
        public Condition() {
            failures = new ArrayList<String>();
        }

        /**
         * Include a failure, causing {@link #isSatisfied()} to fail.
         * @param failure state failure that should be included
         * @return same result, for chaining
         */
        public Condition fail(String failure) {
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
    
}