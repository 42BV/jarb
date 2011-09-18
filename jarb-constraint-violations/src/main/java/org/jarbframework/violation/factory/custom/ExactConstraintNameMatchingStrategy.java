package org.jarbframework.violation.factory.custom;

/**
 * Constraint name has to match the expression exactly.
 * @author Jeroen van Schagen
 * @since Aug 31, 2011
 */
public class ExactConstraintNameMatchingStrategy extends ConstraintNameMatchingStrategy {
    private boolean caseSensitive = false;

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    @Override
    protected boolean nameMatches(String constraintName, String expression) {
        return caseSensitive ? constraintName.equals(expression) : constraintName.equalsIgnoreCase(expression);
    }
}
