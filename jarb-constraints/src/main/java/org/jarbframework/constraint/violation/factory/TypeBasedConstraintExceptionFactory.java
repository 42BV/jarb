package org.jarbframework.constraint.violation.factory;

import org.jarbframework.constraint.violation.CheckFailedException;
import org.jarbframework.constraint.violation.DatabaseConstraintType;
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
    public DatabaseConstraintViolationException buildException(DatabaseConstraintViolation violation, Throwable cause) {
        DatabaseConstraintType constraintType = violation.getConstraintType();
        if (constraintType == null) {
            return new DatabaseConstraintViolationException(violation, cause);
        }
        
        switch (constraintType) {
            case CHECK_FAILED:      return new CheckFailedException(violation, cause);
            case FOREIGN_KEY:       return new ForeignKeyViolationException(violation, cause);
            case INVALID_TYPE:      return new InvalidTypeException(violation, cause);
            case LENGTH_EXCEEDED:   return new LengthExceededException(violation, cause);
            case NOT_NULL:          return new NotNullViolationException(violation, cause);
            case UNIQUE_KEY:        return new UniqueKeyViolationException(violation, cause);
            default:                return new DatabaseConstraintViolationException(violation, cause);
        }
    }

}
