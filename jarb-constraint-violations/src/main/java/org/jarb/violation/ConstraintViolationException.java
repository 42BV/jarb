package org.jarb.violation;

/**
 * Thrown whenever a database constraint violation has occured.
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public abstract class ConstraintViolationException extends RuntimeException {
    private static final long serialVersionUID = -4737155841817649833L;

    /**
     * Constraint violation that occured.
     */
    private final ConstraintViolation violation;

    /**
     * Construct a new {@link ConstraintViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public ConstraintViolationException(ConstraintViolation violation, String message) {
        super(message);
        this.violation = violation;
    }
    
    /**
     * Construct a new {@link ConstraintViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public ConstraintViolationException(ConstraintViolation violation, Throwable cause) {
        super(cause);
        this.violation = violation;
    }
    
    /**
     * Construct a new {@link ConstraintViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public ConstraintViolationException(ConstraintViolation violation, String message, Throwable cause) {
        super(message, cause);
        this.violation = violation;
    }

    /**
     * Retrieve the constraint violation that triggered this exception.
     * @return reference to the constraint violation
     */
    public ConstraintViolation getViolation() {
        return violation;
    }
}
