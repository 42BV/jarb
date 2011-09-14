package org.jarbframework.violation.factory.mapping;

import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.violation.DatabaseConstraintViolation;

/**
 * Capable of matching constraint violations.
 * @author Jeroen van Schagen
 * @since 11 September 2011
 */
public class ConstraintViolationMatcher {
    /** Strategy used to match the violation with our expression. **/
    private ConstraintViolationMatchingStrategy matchingStrategy;
    /** Constraint expression used to describe violations. **/
    private String expression;
    
    public ConstraintViolationMatcher(String expression) {
        this(expression, new ExactConstraintNameMatchingStrategy());
    }
    
    public ConstraintViolationMatcher(String expression, ConstraintViolationMatchingStrategy matchingStrategy) {
        this.expression = expression;
        this.matchingStrategy = notNull(matchingStrategy, "Matching strategy cannot be null");
    }
    
    public boolean matches(DatabaseConstraintViolation violation) {
        return matchingStrategy.matches(violation, expression);
    }
}
