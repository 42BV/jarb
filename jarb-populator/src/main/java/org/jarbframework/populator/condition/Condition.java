package org.jarbframework.populator.condition;

/**
 * Describes a specific condition, which can be checked on our current state.
 */
public interface Condition {
    
    /**
     * Determine if a condition is met, when satisfied we can perform database population.
     * @return the condition check result, see {@link ConditionCheckResult#isSatisfied()}
     */
    ConditionCheckResult check();
    
}