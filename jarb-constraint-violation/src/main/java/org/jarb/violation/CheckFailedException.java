package org.jarb.violation;

/**
 * Exception thrown whenever a database check fails.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class CheckFailedException extends ConstraintViolationException {
    private static final long serialVersionUID = -1855882672284167793L;

    /**
     * Construct a new {@link CheckFailedException}.
     * @param violation constraint violation that triggered this exception
     */
    public CheckFailedException(ConstraintViolation violation) {
        this(violation, "Check '" + violation.getConstraintName() + "' failed.");
    }

    /**
     * Construct a new {@link CheckFailedException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public CheckFailedException(ConstraintViolation violation, String message) {
        super(violation, message);
    }

}
