package org.jarb.violation.resolver;

import org.jarb.violation.ConstraintViolation;

/**
 * Attempts to resolve the constraint violation that caused an exception.
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public interface ConstraintViolationResolver {

    /**
     * Retrieve the constraint violation from a throwable instance.
     * @param throwable exception that should be inspected
     * @return constraint violation, if found in the throwable
     */
    ConstraintViolation resolve(Throwable throwable);

}
