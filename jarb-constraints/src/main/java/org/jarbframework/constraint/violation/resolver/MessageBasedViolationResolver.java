package org.jarbframework.constraint.violation.resolver;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

/**
 * Resolves database constraint violations based on a message.
 * @author Jeroen van Schagen
 */
public interface MessageBasedViolationResolver {

    /**
     * Resolve the database constraint violation.
     * @param message the exception message
     * @return the created database constraint violation
     */
    DatabaseConstraintViolation resolve(String message);
    
}
