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
        this(violation, "Column is of an invalid type.");
    }

    /**
     * Construct a new {@link InvalidTypeException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public InvalidTypeException(ConstraintViolation violation, String message) {
        super(violation, message);
    }

}
