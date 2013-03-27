package org.jarbframework.constraint.violation.factory.custom;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

/**
 * Determines if a constraint violation matches our criteria.
 * @author Jeroen van Schagen
 * @since Aug 31, 2011
 */
public interface DatabaseConstraintViolationMatcher {

    /**
     * Determine if a specific constraint matches our criteria.
     * @param violation database constraint violation being matched
     * @return {@code true} if the violation matches else {@code false}
     */
    boolean matches(DatabaseConstraintViolation violation);

}
