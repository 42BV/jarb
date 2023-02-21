package nl._42.jarb.domain;

import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolationException;
import nl._42.jarb.constraint.violation.factory.DatabaseConstraintExceptionFactory;

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
