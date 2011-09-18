package org.jarbframework.violation.factory.custom;

import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.factory.DatabaseConstraintExceptionFactory;

public class CustomExceptionMapping {
    private final ConstraintViolationMatcher violationMatcher;
    private final DatabaseConstraintExceptionFactory exceptionFactory;
    
    public CustomExceptionMapping(ConstraintViolationMatcher violationMatcher, DatabaseConstraintExceptionFactory exceptionFactory) {
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
