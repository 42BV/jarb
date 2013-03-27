package org.jarbframework.constraint.violation.factory;

import org.jarbframework.constraint.violation.CheckFailedException;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.DatabaseConstraintViolationException;
import org.jarbframework.constraint.violation.ForeignKeyViolationException;
import org.jarbframework.constraint.violation.InvalidTypeException;
import org.jarbframework.constraint.violation.LengthExceededException;
import org.jarbframework.constraint.violation.NotNullViolationException;
import org.jarbframework.constraint.violation.UniqueKeyViolationException;

/**
 * Default exception factory that creates an exception based on type.
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class TypeBasedConstraintExceptionFactory implements DatabaseConstraintExceptionFactory {

    @Override
    public DatabaseConstraintViolationException createException(DatabaseConstraintViolation violation, Throwable cause) {
        DatabaseConstraintViolationException exception = null;
        switch (violation.getConstraintType()) {
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
