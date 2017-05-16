package org.jarbframework.constraint.violation;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.CHECK_FAILED;

/**
 * Exception thrown whenever a database check fails.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class CheckFailedException extends DatabaseConstraintViolationException {

    /**
     * Construct a new {@link CheckFailedException}.
     */
    public CheckFailedException() {
        this(new DatabaseConstraintViolation(CHECK_FAILED));
    }
    
    /**
     * Construct a new {@link CheckFailedException}.
     * @param violation constraint violation that triggered this exception
     */
    public CheckFailedException(DatabaseConstraintViolation violation) {
        this(violation, (Throwable) null);
    }

    /**
     * Construct a new {@link CheckFailedException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public CheckFailedException(DatabaseConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link CheckFailedException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public CheckFailedException(DatabaseConstraintViolation violation, Throwable cause) {
        this(violation, "Check '" + violation.getConstraintName() + "' failed.", cause);
    }

    /**
     * Construct a new {@link CheckFailedException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public CheckFailedException(DatabaseConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
    }
    
}
