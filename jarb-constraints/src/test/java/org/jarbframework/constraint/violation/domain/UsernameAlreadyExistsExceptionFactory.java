package org.jarbframework.constraint.violation.domain;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.DatabaseConstraintViolationException;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;

/**
 * Builds username already exists exceptions.
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class UsernameAlreadyExistsExceptionFactory implements DatabaseConstraintExceptionFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolationException createException(DatabaseConstraintViolation violation, Throwable cause) {
        return new UsernameAlreadyExistsException(violation, cause, this);
    }

}
