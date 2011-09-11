package org.jarbframework.violation.domain;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.DatabaseConstraintViolationException;
import org.jarbframework.violation.factory.DatabaseConstraintExceptionFactory;

/**
 * Builds license number already exists exceptions.
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class LicenseNumberAlreadyExistsExceptionFactory implements DatabaseConstraintExceptionFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolationException createException(DatabaseConstraintViolation violation, Throwable cause) {
        return new LicenseNumberAlreadyExistsException(violation, cause, this);
    }

}
