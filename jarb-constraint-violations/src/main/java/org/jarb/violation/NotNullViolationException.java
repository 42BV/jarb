package org.jarb.violation;

/**
 * Thrown whenever a {@code null} value was provided for a not nullable column.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class NotNullViolationException extends ConstraintViolationException {
    private static final long serialVersionUID = -2973693606821549440L;

    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     */
    public NotNullViolationException(ConstraintViolation violation) {
        this(violation, (Throwable) null);
    }
    
    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public NotNullViolationException(ConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public NotNullViolationException(ConstraintViolation violation, Throwable cause) {
        this(violation, "Column '" + violation.getColumnName() + "' cannot be null.", cause);
    }
 
    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public NotNullViolationException(ConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
        if(violation.getType() != ConstraintViolationType.CANNOT_BE_NULL) {
            throw new IllegalArgumentException("Not null violation exceptions can only be used for not null violations.");
        }
    }
}
