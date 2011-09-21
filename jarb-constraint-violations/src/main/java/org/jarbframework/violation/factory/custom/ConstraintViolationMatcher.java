package org.jarbframework.violation.factory.custom;

import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.violation.DatabaseConstraintViolation;

/**
 * Determines if an expression matches some constraint violation.
 * @author Jeroen van Schagen
 * @since 11 September 2011
 */
public final class ConstraintViolationMatcher {
    /** Constraint expression used to describe violations. **/
    private final String expression;
    /** Strategy used to match the violation with our expression. **/
    private final ConstraintViolationMatchingStrategy strategy;

    /**
     * Construct a new constraint violation matcher.
     * @param expression violation expression
     * @param strategy matching strategy
     */
    public ConstraintViolationMatcher(String expression, ConstraintViolationMatchingStrategy strategy) {
        this.expression = expression;
        this.strategy = notNull(strategy, "Strategy cannot be null.");
    }
    
    /**
     * Construct a new "exact" constraint name based matcher.
     * @param constraintName expected constraint name
     * @return new violation matcher instance
     */
    public static ConstraintViolationMatcher name(String constraintName) {
        return new ConstraintViolationMatcher(constraintName, new ExactConstraintNameMatchingStrategy());
    }
    
    /**
     * Construct a new "regex" constraint name based matcher.
     * @param constraintPattern pattern that constraint names should match
     * @return new violation matcher instance
     */
    public static ConstraintViolationMatcher regex(String constraintPattern) {
        return new ConstraintViolationMatcher(constraintPattern, new RegexConstraintNameMatchingStrategy());
    }
    
    /**
     * Determine if a constraint violation is supported.
     * @param violation the constraint violation to check
     * @return {@code true} if the violation is supported, else {@code false}
     */
    public boolean supports(DatabaseConstraintViolation violation) {
        return strategy.matches(violation, expression);
    }
    
}
