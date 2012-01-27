package org.jarbframework.violation.factory.custom;

import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.factory.DatabaseConstraintExceptionFactory;

/**
 * Describes the mapping of an exception factory on constraint violations.
 * @author Jeroen van Schagen
 * @since 17-09-2011
 */
public class ExceptionFactoryMapping {
    /** Determines whether a constraint violation is supported by this mapping. **/
    private final ConstraintViolationMatcher violationMatcher;
    /** Enables a constraint violation to be translated into an exception. **/
    private final DatabaseConstraintExceptionFactory exceptionFactory;

    public ExceptionFactoryMapping(ConstraintViolationMatcher violationMatcher, DatabaseConstraintExceptionFactory exceptionFactory) {
        this.violationMatcher = notNull(violationMatcher, "Violation matcher cannot be null.");
        this.exceptionFactory = notNull(exceptionFactory, "Exception factory cannot be null.");
    }

    public boolean supports(DatabaseConstraintViolation violation) {
        return violationMatcher.matches(violation);
    }

    public DatabaseConstraintExceptionFactory getExceptionFactory() {
        return exceptionFactory;
    }
}
