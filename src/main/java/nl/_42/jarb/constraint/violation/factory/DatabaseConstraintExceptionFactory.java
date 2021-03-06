package nl._42.jarb.constraint.violation.factory;

import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;

/**
 * Creates an exception to notify about our database constraint violation.
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public interface DatabaseConstraintExceptionFactory {

    /**
     * Build the exception for some constraint violation.
     * @param violation reference to our constraint violation
     * @param cause original exception that triggered our violation
     * @return exception that describes our violation
     */
    Throwable buildException(DatabaseConstraintViolation violation, Throwable cause);

}
