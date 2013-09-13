package org.jarbframework.constraint.violation.domain;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.UniqueKeyViolationException;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.NamedConstraint;

/**
 * Car licenses can only be used once.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
@NamedConstraint("uk_cars_license_number")
public class CarAlreadyExistsException extends UniqueKeyViolationException {

    private DatabaseConstraintExceptionFactory exceptionFactory;

    public CarAlreadyExistsException(DatabaseConstraintViolation violation) {
        super(violation);
    }

    public CarAlreadyExistsException(DatabaseConstraintViolation violation, Throwable cause) {
        super(violation, cause);
    }

    public CarAlreadyExistsException(DatabaseConstraintViolation violation, Throwable cause, DatabaseConstraintExceptionFactory exceptionFactory) {
        super(violation, cause);
        this.exceptionFactory = exceptionFactory;
    }

    public DatabaseConstraintExceptionFactory getExceptionFactory() {
        return exceptionFactory;
    }

}
