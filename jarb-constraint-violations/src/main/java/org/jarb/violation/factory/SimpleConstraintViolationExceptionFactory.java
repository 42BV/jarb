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
            exception = new NotNullViolationException(violation, cause);
            break;
        case CHECK_FAILED:
            exception = new CheckFailedException(violation, cause);
            break;
        case INVALID_TYPE:
            exception = new InvalidTypeException(violation, cause);
            break;
        case LENGTH_EXCEEDED:
            exception = new LengthExceededException(violation, cause);
            break;
        case UNIQUE_VIOLATION:
            exception = new UniqueKeyViolationException(violation, cause);
            break;
        }
        return exception;
    }

}
