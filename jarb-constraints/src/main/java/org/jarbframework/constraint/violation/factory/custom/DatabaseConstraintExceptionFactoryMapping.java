package org.jarbframework.constraint.violation.factory.custom;

import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;

/**
 * Describes the mapping of an exception factory on constraint violations.
 * @author Jeroen van Schagen
 * @since 17-09-2011
 */
public class DatabaseConstraintExceptionFactoryMapping {
    
    private final DatabaseConstraintViolationMatcher violationMatcher;
    
    private final DatabaseConstraintExceptionFactory exceptionFactory;

    public DatabaseConstraintExceptionFactoryMapping(DatabaseConstraintViolationMatcher violationMatcher, DatabaseConstraintExceptionFactory exceptionFactory) {
        this.violationMatcher = notNull(violationMatcher, "Violation matcher cannot be null.");
        this.exceptionFactory = notNull(exceptionFactory, "Exception factory cannot be null.");
    }

    public boolean isSupported(DatabaseConstraintViolation violation) {
        return violationMatcher.matches(violation);
    }

    public DatabaseConstraintExceptionFactory getExceptionFactory() {
        return exceptionFactory;
    }
    
}
