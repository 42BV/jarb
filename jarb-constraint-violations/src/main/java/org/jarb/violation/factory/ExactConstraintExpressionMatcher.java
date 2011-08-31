package org.jarb.violation.factory;

import org.apache.commons.lang3.StringUtils;

/**
 * Constraint name has to match the expression exactly.
 * @author Jeroen van Schagen
 * @since Aug 31, 2011
 */
public class ExactConstraintExpressionMatcher implements ConstraintExpressionMatcher {
    
    @Override
    public boolean matches(String constraintName, String constraintExpression) {
        return StringUtils.equals(constraintExpression, constraintName);
    }
    
}
