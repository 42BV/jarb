package org.jarb.violation;

/**
 * Thrown whenever the expression value is longer than our column length.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class LengthExceededException extends ConstraintViolationException {
    private static final long serialVersionUID = -5897315011236651753L;

    /**
     * Construct a new {@link LengthExceededException}.
     * @param violation constraint violation that triggered this exception
     */
    public LengthExceededException(ConstraintViolation violation) {
        this(violation, (Throwable) null);
    }
    
    /**
     * Construct a new {@link LengthExceededException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public LengthExceededException(ConstraintViolation violation, String message) {
        this(violation, message, null);
    }
    
    /**
     * Construct a new {@link LengthExceededException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public LengthExceededException(ConstraintViolation violation, Throwable cause) {
        this(violation, "Column maximum length was exceeded.", cause);
    }
 
    /**
     * Construct a new {@link LengthExceededException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public LengthExceededException(ConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
        if(violation.getType() != ConstraintViolationType.LENGTH_EXCEEDED) {
            throw new IllegalArgumentException("Length exceeded exceptions can only be used for length exceeded violations.");
        }
    }
}
