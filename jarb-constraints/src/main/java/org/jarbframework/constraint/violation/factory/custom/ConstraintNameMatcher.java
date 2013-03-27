/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.constraint.violation.factory.custom;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

/**
 * Matches violations based on their constraint name.
 * @author Jeroen van Schagen
 * @since Sep 2, 2011
 */
public abstract class ConstraintNameMatcher implements DatabaseConstraintViolationMatcher {
    
    private final String expression;
    
    private boolean ignoreCase = true;

    public ConstraintNameMatcher(String expression) {
        this.expression = expression;
    }

    public static ConstraintNameMatcher exact(String name) {
        return new ExactConstraintNameMatcher(name);
    }

    public static ConstraintNameMatcher regex(String namePattern) {
        return new RegexConstraintNameMatcher(namePattern);
    }

    @Override
    public final boolean matches(DatabaseConstraintViolation violation) {
        return matches(violation.getConstraintName());
    }

    /**
     * Determine if the constraint name matches an expression.
     * @param constraintName expected constraint expression, that should match
     * @return {@code true} if the name matches our expression, else {@code false}
     */
    protected abstract boolean matches(String constraintName);

    public String getExpression() {
        return expression;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public ConstraintNameMatcher setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        return this;
    }

    private static class ExactConstraintNameMatcher extends ConstraintNameMatcher {

        public ExactConstraintNameMatcher(String name) {
            super(name);
        }

        @Override
        protected boolean matches(String constraintName) {
            boolean matches;
            if (isIgnoreCase()) {
                matches = StringUtils.endsWithIgnoreCase(constraintName, getExpression());
            } else {
                matches = StringUtils.equals(constraintName, getExpression());
            }
            return matches;
        }
    }

    private static class RegexConstraintNameMatcher extends ConstraintNameMatcher {

        public RegexConstraintNameMatcher(String pattern) {
            super(pattern);
        }

        @Override
        protected boolean matches(String constraintName) {
            String pattern = getExpression();
            if (isIgnoreCase()) {
                constraintName = lowerCase(constraintName);
                pattern = lowerCase(pattern);
            }
            return isNotBlank(constraintName) && constraintName.matches(pattern);
        }
    }

}
