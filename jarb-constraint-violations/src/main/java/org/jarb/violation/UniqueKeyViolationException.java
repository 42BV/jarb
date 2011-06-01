package org.jarb.violation;

/**
 * Thrown whenever a unique key value already exists.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class UniqueKeyViolationException extends ConstraintViolationException {
    private static final long serialVersionUID = -5771965201756493983L;

    /**
     * Construct a new {@link UniqueKeyViolationException}.
     * @param violation constraint violation that triggered this exception
     */
    public UniqueKeyViolationException(ConstraintViolation violation) {
        this(violation, "Unique key '" + violation.getConstraintName() + "' was violated.");
    }

    /**
     * Construct a new {@link UniqueKeyViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public UniqueKeyViolationException(ConstraintViolation violation, String message) {
        super(violation, message);
    }

}
