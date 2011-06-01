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
        this(violation, "Column maximum length was exceeded.");
    }

    /**
     * Construct a new {@link LengthExceededException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public LengthExceededException(ConstraintViolation violation, String message) {
        super(violation, message);
    }

}
