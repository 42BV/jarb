package org.jarbframework.constraint.violation;

import static org.jarbframework.utils.Asserts.notNull;

/**
 * Thrown whenever a database constraint violation has occured.
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public abstract class DatabaseConstraintViolationException extends RuntimeException {

    /** Constraint violation that occured. */
    private final DatabaseConstraintViolation violation;

    /**
     * Construct a new {@link DatabaseConstraintViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public DatabaseConstraintViolationException(DatabaseConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link DatabaseConstraintViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public DatabaseConstraintViolationException(DatabaseConstraintViolation violation, Throwable cause) {
        this(violation, "Database constraint was violated.", cause);
    }

    /**
     * Construct a new {@link DatabaseConstraintViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public DatabaseConstraintViolationException(DatabaseConstraintViolation violation, String message, Throwable cause) {
        super(message, cause);
        this.violation = notNull(violation, "Constraint violation cannot be null.");
    }

    /**
     * Retrieve the constraint violation that triggered this exception.
     * @return reference to the constraint violation
     */
    public DatabaseConstraintViolation getViolation() {
        return violation;
    }
}
