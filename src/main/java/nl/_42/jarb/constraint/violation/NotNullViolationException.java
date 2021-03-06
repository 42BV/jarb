package nl._42.jarb.constraint.violation;

import static nl._42.jarb.constraint.violation.DatabaseConstraintType.NOT_NULL;

/**
 * Thrown whenever a {@code null} value was provided for a not nullable column.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class NotNullViolationException extends DatabaseConstraintViolationException {

    /**
     * Construct a new {@link NotNullViolationException}.
     */
    public NotNullViolationException() {
        this(new DatabaseConstraintViolation(NOT_NULL));
    }
    
    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     */
    public NotNullViolationException(DatabaseConstraintViolation violation) {
        this(violation, (Throwable) null);
    }

    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public NotNullViolationException(DatabaseConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public NotNullViolationException(DatabaseConstraintViolation violation, Throwable cause) {
        this(violation, "Column '" + violation.getColumnName() + "' cannot be null.", cause);
    }

    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public NotNullViolationException(DatabaseConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
    }
    
}
