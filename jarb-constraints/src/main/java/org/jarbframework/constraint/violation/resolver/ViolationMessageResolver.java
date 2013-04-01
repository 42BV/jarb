package org.jarbframework.constraint.violation.resolver;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

public interface ViolationMessageResolver {

    DatabaseConstraintViolation resolveByMessage(String message);
    
}
