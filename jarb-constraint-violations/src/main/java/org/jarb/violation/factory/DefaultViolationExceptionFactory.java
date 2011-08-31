package org.jarb.violation.factory;

import org.jarb.violation.CheckFailedException;
import org.jarb.violation.DatabaseConstraintViolation;
import org.jarb.violation.DatabaseConstraintViolationException;
import org.jarb.violation.ForeignKeyViolationException;
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
public class DefaultViolationExceptionFactory implements DatabaseConstraintViolationExceptionFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolationException createException(DatabaseConstraintViolation violation, Throwable cause) {
        DatabaseConstraintViolationException exception = null;
        switch (violation.getViolationType()) {
        case NOT_NULL:
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
        case UNIQUE_KEY:
            exception = new UniqueKeyViolationException(violation, cause);
            break;
        case FOREIGN_KEY:
            exception = new ForeignKeyViolationException(violation, cause);
            break;
        }
        return exception;
    }

}
