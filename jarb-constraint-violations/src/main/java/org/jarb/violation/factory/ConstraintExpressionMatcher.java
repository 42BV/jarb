package org.jarb.violation.factory;

/**
 * Determines if a constraint name matches an expression.
 * @author Jeroen van Schagen
 * @since Aug 31, 2011
 */
public interface ConstraintExpressionMatcher {

    /**
     * Determine if a specific constraint name matches a constraint expression.
     * @param constraintName constraint name being matched
     * @param constraintExpression expression being matched against
     * @return {@code true} if the name matches our expression, else {@code false}
     */
    boolean matches(String constraintName, String constraintExpression);
    
}
