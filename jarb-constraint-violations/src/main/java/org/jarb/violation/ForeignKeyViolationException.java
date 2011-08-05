/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.violation;

/**
 * Thrown whenever a foreign key constraint has been violated.
 *
 * @author Jeroen van Schagen
 * @date Aug 5, 2011
 */
public class ForeignKeyViolationException extends ConstraintViolationException {

    /**
     * Construct a new {@link ForeignKeyViolationException}.
     * @param violation constraint violation that triggered this exception
     */
    public ForeignKeyViolationException(ConstraintViolation violation) {
        this(violation, (Throwable) null);
    }

    /**
     * Construct a new {@link ForeignKeyViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     */
    public ForeignKeyViolationException(ConstraintViolation violation, String message) {
        this(violation, message, null);
    }

    /**
     * Construct a new {@link ForeignKeyViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public ForeignKeyViolationException(ConstraintViolation violation, Throwable cause) {
        this(violation, "Column '" + violation.getColumnName() + "' cannot be null.", cause);
    }

    /**
     * Construct a new {@link ForeignKeyViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public ForeignKeyViolationException(ConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
        if (violation.getType() != ConstraintViolationType.NOT_NULL) {
            throw new IllegalArgumentException("Not null violation exceptions can only be used for not null violations.");
        }
    }

}
