package org.jarbframework.violation;

import static org.jarbframework.utils.Conditions.state;
import static org.jarbframework.violation.DatabaseConstraintViolationType.NOT_NULL;

/**
 * Thrown whenever a {@code null} value was provided for a not nullable column.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class NotNullViolationException extends DatabaseConstraintViolationException {
    private static final long serialVersionUID = -2973693606821549440L;

    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     */
    public NotNullViolationException(DatabaseConstraintViolation violation) {
        this(violation, (Throwable) null);
    }

    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public NotNullViolationException(DatabaseConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public NotNullViolationException(DatabaseConstraintViolation violation, Throwable cause) {
        this(violation, "Column '" + violation.getColumnName() + "' cannot be null.", cause);
    }

    /**
     * Construct a new {@link NotNullViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public NotNullViolationException(DatabaseConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
        state(violation.getViolationType() == NOT_NULL, "Not null exception can only occur for not null violations");
    }
}
