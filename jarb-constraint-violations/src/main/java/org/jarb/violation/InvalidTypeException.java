package org.jarb.violation;

/**
 * Thrown whenever the expression type does not match the column type.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class InvalidTypeException extends ConstraintViolationException {
    private static final long serialVersionUID = -4828293558481913477L;

    /**
     * Construct a new {@link InvalidTypeException}.
     * @param violation constraint violation that triggered this exception
     */
    public InvalidTypeException(ConstraintViolation violation) {
        this(violation, (Throwable) null);
    }
    
    /**
     * Construct a new {@link InvalidTypeException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public InvalidTypeException(ConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link InvalidTypeException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public InvalidTypeException(ConstraintViolation violation, Throwable cause) {
        this(violation, "Column is of an invalid type.", cause);
    }
 
    /**
     * Construct a new {@link InvalidTypeException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public InvalidTypeException(ConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
        if(violation.getType() != ConstraintViolationType.INVALID_TYPE) {
            throw new IllegalArgumentException("Invalid type exceptions can only be used for invalid type violations.");
        }
    }
}
