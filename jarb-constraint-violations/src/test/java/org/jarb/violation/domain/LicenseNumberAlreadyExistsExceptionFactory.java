package org.jarb.violation.domain;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationException;
import org.jarb.violation.factory.ConstraintViolationExceptionFactory;

/**
 * Builds license number already exists exceptions.
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class LicenseNumberAlreadyExistsExceptionFactory implements ConstraintViolationExceptionFactory {

    /** {@inheritDoc} **/
    @Override
    public ConstraintViolationException createException(ConstraintViolation violation, Throwable cause) {
        return new LicenseNumberAlreadyExistsException(violation, this); // Provide factory reference
    }

}
