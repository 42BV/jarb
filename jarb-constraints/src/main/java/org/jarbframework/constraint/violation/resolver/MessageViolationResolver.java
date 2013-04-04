package org.jarbframework.constraint.violation.resolver;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

public interface MessageViolationResolver {

    DatabaseConstraintViolation resolve(String message);
    
}
