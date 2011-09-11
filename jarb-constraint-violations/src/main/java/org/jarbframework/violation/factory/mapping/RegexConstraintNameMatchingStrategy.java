package org.jarbframework.violation.factory.mapping;

import static org.apache.commons.lang3.StringUtils.lowerCase;

/**
 * Constraint name has to match the expression regex.
 * @author Jeroen van Schagen
 * @since Aug 31, 2011
 */
public class RegexConstraintNameMatchingStrategy extends ConstraintNameMatchingStrategy {
    private boolean caseSensitive = false;

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    @Override
    protected boolean nameMatches(String constraintName, String expression) {
        if (!caseSensitive) {
            constraintName = lowerCase(constraintName);
            expression = lowerCase(expression);
        }
        return constraintName.matches(expression);
    }

}
