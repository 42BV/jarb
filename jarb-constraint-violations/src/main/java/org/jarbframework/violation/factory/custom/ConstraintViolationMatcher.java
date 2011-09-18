package org.jarbframework.violation.factory.custom;

import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.violation.DatabaseConstraintViolation;

/**
 * Capable of matching constraint violations.
 * @author Jeroen van Schagen
 * @since 11 September 2011
 */
public class ConstraintViolationMatcher {
    /** Constraint expression used to describe violations. **/
    private final String expression;
    /** Strategy used to match the violation with our expression. **/
    private final ConstraintViolationMatchingStrategy strategy;

    public ConstraintViolationMatcher(String expression, ConstraintViolationMatchingStrategy strategy) {
        this.expression = expression;
        this.strategy = notNull(strategy, "Strategy cannot be null.");
    }
    
    public static ConstraintViolationMatcher name(String constraintName) {
        return new ConstraintViolationMatcher(constraintName, new ExactConstraintNameMatchingStrategy());
    }
    
    public static ConstraintViolationMatcher regex(String constraintPattern) {
        return new ConstraintViolationMatcher(constraintPattern, new RegexConstraintNameMatchingStrategy());
    }
    
    public boolean matches(DatabaseConstraintViolation violation) {
        return strategy.matches(violation, expression);
    }
}
