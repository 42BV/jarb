package org.jarbframework.validation;

import org.jarbframework.utils.bean.PropertyReference;

public class DatabaseConstraintViolation {
    private final PropertyReference propertyRef;
    private final String message;
    
    public DatabaseConstraintViolation(PropertyReference propertyRef, String message) {
        this.propertyRef = propertyRef;
        this.message = message;
    }
    
    public PropertyReference getPropertyRef() {
        return propertyRef;
    }
    
    public String getMessage() {
        return message;
    }
}