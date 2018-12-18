package nl._42.jarb.constraint.violation;

import static nl._42.jarb.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;

/**
 * Thrown whenever a unique key value already exists.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class UniqueKeyViolationException extends DatabaseConstraintViolationException {

    /**
     * Construct a new {@link UniqueKeyViolationException}.
     */
    public UniqueKeyViolationException() {
        this(new DatabaseConstraintViolation(UNIQUE_KEY));
    }
    
    /**
     * Construct a new {@link UniqueKeyViolationException}.
     * @param violation constraint violation that triggered this exception
     */
    public UniqueKeyViolationException(DatabaseConstraintViolation violation) {
        this(violation, (Throwable) null);
    }

    /**
     * Construct a new {@link UniqueKeyViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public UniqueKeyViolationException(DatabaseConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link UniqueKeyViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public UniqueKeyViolationException(DatabaseConstraintViolation violation, Throwable cause) {
        this(violation, "Unique key '" + violation.getConstraintName() + "' was violated.", cause);
    }

    /**
     * Construct a new {@link UniqueKeyViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public UniqueKeyViolationException(DatabaseConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
    }
    
}
