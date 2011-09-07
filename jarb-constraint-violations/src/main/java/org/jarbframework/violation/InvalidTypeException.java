package org.jarbframework.violation;

import static org.jarbframework.utils.Conditions.state;
import static org.jarbframework.violation.DatabaseConstraintViolationType.INVALID_TYPE;

/**
 * Thrown whenever the expression type does not match the column type.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class InvalidTypeException extends DatabaseConstraintViolationException {
    private static final long serialVersionUID = -4828293558481913477L;

    /**
     * Construct a new {@link InvalidTypeException}.
     * @param violation constraint violation that triggered this exception
     */
    public InvalidTypeException(DatabaseConstraintViolation violation) {
        this(violation, (Throwable) null);
    }

    /**
     * Construct a new {@link InvalidTypeException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public InvalidTypeException(DatabaseConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link InvalidTypeException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public InvalidTypeException(DatabaseConstraintViolation violation, Throwable cause) {
        this(violation, "Column is of an invalid type.", cause);
    }

    /**
     * Construct a new {@link InvalidTypeException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public InvalidTypeException(DatabaseConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
        state(violation.getViolationType() == INVALID_TYPE, "Invalid type exception can only occur for invalid type violations.");
    }
}
