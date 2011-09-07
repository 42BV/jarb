package org.jarbframework.violation.factory;

import org.jarbframework.violation.DatabaseConstraintViolation;

/**
 * Builds the exception for constraint violations.
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public interface DatabaseConstraintViolationExceptionFactory {

    /**
     * Build the exception for some constraint violation.
     * @param violation reference to our constraint violation
     * @param cause origional exception that triggered our violation
     * @return exception that describes our violation
     */
    Throwable createException(DatabaseConstraintViolation violation, Throwable cause);

}
