package org.jarb.violation;

import static org.springframework.util.Assert.state;

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
        this(violation, (Throwable) null);
    }

    /**
     * Construct a new {@link CheckFailedException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public CheckFailedException(ConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link CheckFailedException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public CheckFailedException(ConstraintViolation violation, Throwable cause) {
        this(violation, "Check '" + violation.getConstraintName() + "' failed.", cause);
    }

    /**
     * Construct a new {@link CheckFailedException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public CheckFailedException(ConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
        state(violation.getType() == ConstraintViolationType.CHECK_FAILED, "Check failed exception can only occur for check violations");
    }
}
