package org.jarbframework.populator.condition;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Evaluates whether as specific condition has been satisfied.
 * @author Jeroen van Schagen
 * @since 21-06-2011
 */
public interface Condition {

    /**
     * Determine if a condition has been satisfied.
     */
    ConditionEvaluation evaluate();

    /**
     * Capable of building {@link ConditionEvaluation}.
     */
    public class ConditionEvaluationBuilder {
        private final List<String> failures = new ArrayList<String>();

        /**
         * Create a new condition evaluation builder.
         * @return new builder
         */
        public static ConditionEvaluationBuilder evaluation() {
        	return new ConditionEvaluationBuilder();
        }
        
        /**
         * Check whether a certain state is {@code true}, whenever not achieved
         * include a failure using the {@link #addFailure(String)}.
         * @param state the state that should be satisfied
         * @param message failure message to include when unsatisfied
         * @return this evaluation builder, for chaining
         */
        public ConditionEvaluationBuilder state(boolean state, String message) {
            if (!state) {
                fail(message);
            }
            return this;
        }

        /**
         * Include a failure, causing {@link #isSatisfied()} to fail.
         * @param message failure message to include
         * @return this evaluation builder, for chaining
         */
        public ConditionEvaluationBuilder fail(String message) {
            failures.add(message);
            return this;
        }
        
        /**
         * Build a {@link ConditionEvaluation}.
         * @return new evaluation, containing our messages
         */
        public ConditionEvaluation build() {
        	return new ConditionEvaluation(failures);
        }
    }
    
    /**
     * Result type from {@link Condition#evaluate()}. The condition has been
     * met whenever {@link #isSatisfied()} evaluates into {@code true}. If
     * the condition is not satisfied, look at {@link #getFailures()}.
     */
    public class ConditionEvaluation {
        private final List<String> failures;
        
        private ConditionEvaluation(Collection<String> failures) {
        	this.failures = new ArrayList<String>(failures);
        }
        
        /**
         * Create a "successful" evaluation, meaning the condition is satisfied.
         * @return new successful evaluation
         */
        public static ConditionEvaluation success() {
        	return new ConditionEvaluation(Collections.<String> emptyList());
        }
        
        /**
         * Create a "failed" evaluation, meaning the condition is unsatisfied.
         * @param failure the message of our failure
         * @return new failed evaluation
         */
        public static ConditionEvaluation fail(String failure) {
        	return new ConditionEvaluation(Collections.singleton(failure));
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
