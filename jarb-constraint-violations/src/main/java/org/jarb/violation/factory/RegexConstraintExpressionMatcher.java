package org.jarb.violation.factory;

/**
 * Constraint name has to match the expression regex.
 * @author Jeroen van Schagen
 * @since Aug 31, 2011
 */
public class RegexConstraintExpressionMatcher implements ConstraintExpressionMatcher {

    @Override
    public boolean matches(String constraintName, String constraintExpression) {
        return constraintName.matches(constraintExpression);
    }

}
