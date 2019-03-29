package nl._42.jarb.constraint.violation.factory;

import nl._42.jarb.constraint.violation.CheckFailedException;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolationException;
import nl._42.jarb.constraint.violation.ExclusionException;
import nl._42.jarb.constraint.violation.ForeignKeyViolationException;
import nl._42.jarb.constraint.violation.InvalidTypeException;
import nl._42.jarb.constraint.violation.LengthExceededException;
import nl._42.jarb.constraint.violation.NotNullViolationException;
import nl._42.jarb.constraint.violation.UniqueKeyViolationException;

/**
 * Default exception factory that creates an exception based on type.
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class DefaultConstraintExceptionFactory implements DatabaseConstraintExceptionFactory {

    @Override
    public DatabaseConstraintViolationException buildException(DatabaseConstraintViolation violation, Throwable cause) {
        if (violation.getConstraintType() == null) {
            return new DatabaseConstraintViolationException(violation, cause);
        }
        
        switch (violation.getConstraintType()) {
            case CHECK_FAILED:      return new CheckFailedException(violation, cause);
            case FOREIGN_KEY:       return new ForeignKeyViolationException(violation, cause);
            case INVALID_TYPE:      return new InvalidTypeException(violation, cause);
            case LENGTH_EXCEEDED:   return new LengthExceededException(violation, cause);
            case NOT_NULL:          return new NotNullViolationException(violation, cause);
            case UNIQUE_KEY:        return new UniqueKeyViolationException(violation, cause);
            case EXCLUSION:         return new ExclusionException(violation, cause);
            default:                return new DatabaseConstraintViolationException(violation, cause);
        }
    }

}
