package org.jarbframework.populator.condition;

/**
 * Determines if the desired condition is met.
 */
public interface ConditionChecker {
    
    /**
     * Determine if a condition is met, whenever {@code true} we will perform our database population.
     * @return {@code true} if the desired condition has been met, else {@code false}
     */
    ConditionCheckResult checkCondition();
    
}