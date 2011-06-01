package org.jarb.violation.factory;

import org.jarb.violation.ConstraintViolation;

/**
 * Builds the exception for constraint violations.
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public interface ConstraintViolationExceptionFactory {

    /**
     * Build the exception for some constraint violation.
     * @param violation reference to our constraint violation
     * @return exception that describes our violation
     */
    Throwable createException(ConstraintViolation violation);

}
