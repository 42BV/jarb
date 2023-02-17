package nl._42.jarb.domain;

import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.UniqueKeyViolationException;
import nl._42.jarb.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.factory.NamedConstraint;

import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.UniqueKeyViolationException;
import nl._42.jarb.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.factory.NamedConstraint;

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
