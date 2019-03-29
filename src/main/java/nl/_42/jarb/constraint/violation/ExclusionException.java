package nl._42.jarb.constraint.violation;

import static nl._42.jarb.constraint.violation.DatabaseConstraintType.EXCLUSION;

/**
 * Exception thrown whenever an exclusion constraint fails.
 * 
 * @author Sander Benschop
 * @since 29-03-2019
 */
public class ExclusionException extends DatabaseConstraintViolationException {

    /**
     * Construct a new {@link ExclusionException}.
     */
    public ExclusionException() {
        this(new DatabaseConstraintViolation(EXCLUSION));
    }

    /**
     * Construct a new {@link ExclusionException}.
     * @param violation constraint violation that triggered this exception
     */
    public ExclusionException(DatabaseConstraintViolation violation) {
        this(violation, (Throwable) null);
    }

    /**
     * Construct a new {@link ExclusionException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public ExclusionException(DatabaseConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link ExclusionException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public ExclusionException(DatabaseConstraintViolation violation, Throwable cause) {
        this(violation, "Exclusion '" + violation.getConstraintName() + "' failed.", cause);
    }

    /**
     * Construct a new {@link ExclusionException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public ExclusionException(DatabaseConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
    }
    
}
