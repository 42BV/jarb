package org.jarb.violation.domain;

import org.jarb.violation.DatabaseConstraintViolation;
import org.jarb.violation.DatabaseConstraintViolationException;
import org.jarb.violation.factory.ConstraintViolationExceptionFactory;

/**
 * Builds license number already exists exceptions.
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class LicenseNumberAlreadyExistsExceptionFactory implements ConstraintViolationExceptionFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolationException createException(DatabaseConstraintViolation violation, Throwable cause) {
        return new LicenseNumberAlreadyExistsException(violation, cause, this);
    }

}
