package org.jarbframework.constraint.violation.domain;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.DatabaseConstraintViolationException;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;

/**
 * Builds car already exists exceptions.
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class CarAlreadyExistsExceptionFactory implements DatabaseConstraintExceptionFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolationException buildException(DatabaseConstraintViolation violation, Throwable cause) {
        return new CarAlreadyExistsException(violation, cause, this);
    }

}
