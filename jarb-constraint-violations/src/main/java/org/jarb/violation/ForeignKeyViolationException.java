/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.violation;

import static org.springframework.util.Assert.state;

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
        this(violation, "Foreign key '" + violation.getConstraintName() + "' was violated.", cause);
    }

    /**
     * Construct a new {@link ForeignKeyViolationException}.
     * @param violation constraint violation that triggered this exception
     * @param message exception message that should be shown
     * @param cause the cause of this constraint violation exception, can be {@code null}
     */
    public ForeignKeyViolationException(ConstraintViolation violation, String message, Throwable cause) {
        super(violation, message, cause);
        state(violation.getType() == ConstraintViolationType.FOREIGN_KEY, "Foreign key exception can only occur for foreign key violations");
    }

}
