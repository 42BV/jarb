package org.jarbframework.constraint.violation.domain;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.UniqueKeyViolationException;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;

/**
 * User names can only be used once.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class UsernameAlreadyExistsException extends UniqueKeyViolationException {

    private DatabaseConstraintExceptionFactory exceptionFactory;

    public UsernameAlreadyExistsException(DatabaseConstraintViolation violation) {
        super(violation);
    }

    public UsernameAlreadyExistsException(DatabaseConstraintViolation violation, Throwable cause) {
        super(violation, cause);
    }

    public UsernameAlreadyExistsException(DatabaseConstraintViolation violation, Throwable cause, DatabaseConstraintExceptionFactory exceptionFactory) {
        super(violation, cause);
        this.exceptionFactory = exceptionFactory;
    }

    /**
     * Retrieve the factory that generated this exception.
     * @return exception factory
     */
    public DatabaseConstraintExceptionFactory getExceptionFactory() {
        return exceptionFactory;
    }

}
