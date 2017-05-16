package org.jarbframework.constraint.violation;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;

/**
 * Thrown whenever the expression value is longer than our column length.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class LengthExceededException extends DatabaseConstraintViolationException {

    /**
     * Construct a new {@link LengthExceededException}.
     */
    public LengthExceededException() {
        this(new DatabaseConstraintViolation(LENGTH_EXCEEDED));
    }
    
    /**
     * Construct a new {@link LengthExceededException}.
     * @param violation constraint violation that triggered this exception
     */
    public LengthExceededException(DatabaseConstraintViolation violation) {
        this(violation, (Throwable) null);
    }

    /**
     * Construct a new {@link LengthExceededException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public LengthExceededException(DatabaseConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link LengthExceededException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public LengthExceededException(DatabaseConstraintViolation violation, Throwable cause) {
        this(violation, "Column maximum length was exceeded.", cause);
    }

    /**
     * Construct a new {@link LengthExceededException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public LengthExceededException(DatabaseConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
    }
    
}
