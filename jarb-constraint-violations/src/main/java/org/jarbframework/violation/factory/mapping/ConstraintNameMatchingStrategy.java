/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.violation.factory.mapping;

import static org.jarbframework.utils.Asserts.notNull;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.violation.DatabaseConstraintViolation;

/**
 * Matches violations based on their constraint name.
 *
 * @author Jeroen van Schagen
 * @date Sep 2, 2011
 */
public abstract class ConstraintNameMatchingStrategy implements ConstraintViolationMatchingStrategy {

    @Override
    public final boolean matches(DatabaseConstraintViolation violation, String expression) {
        String constraintName = notNull(violation, "Constraint violation cannot be null").getConstraintName();
        if (StringUtils.isBlank(constraintName)) {
            return false;
        }
        return nameMatches(constraintName, expression);
    }

    /**
     * Determine if the constraint name matches an expression.
     * @param constraintName name of the constraint (never {@code null})
     * @param expression the expression being matched
     * @return {@code true} if the name matches our expression, else {@code false}
     */
    protected abstract boolean nameMatches(String constraintName, String expression);

}
