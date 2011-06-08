package org.jarb.violation.factory;

import org.jarb.violation.CheckFailedException;
import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationException;
import org.jarb.violation.InvalidTypeException;
import org.jarb.violation.LengthExceededException;
import org.jarb.violation.NotNullViolationException;
import org.jarb.violation.UniqueKeyViolationException;

/**
 * Provides a type specific constraint violation exception.
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class SimpleConstraintViolationExceptionFactory implements ConstraintViolationExceptionFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstraintViolationException createException(ConstraintViolation violation, Throwable cause) {
        ConstraintViolationException exception = null;
        switch (violation.getType()) {
        case CANNOT_BE_NULL:
            exception = new NotNullViolationException(violation);
            break;
        case CHECK_FAILED:
            exception = new CheckFailedException(violation);
            break;
        case INVALID_TYPE:
            exception = new InvalidTypeException(violation);
            break;
        case LENGTH_EXCEEDED:
            exception = new LengthExceededException(violation);
            break;
        case UNIQUE_VIOLATION:
            exception = new UniqueKeyViolationException(violation);
            break;
        }
        return exception;
    }

}
