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
        this(violation, "Column '" + violation.getColumnName() + "' cannot be null.");
    }

    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public NotNullViolationException(ConstraintViolation violation, String message) {
        super(violation, message);
    }

}
