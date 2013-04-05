package org.jarbframework.constraint.violation.resolver.recognize;

public interface DatabaseProductSpecific {
    
    boolean supports(DatabaseProduct product);
    
}
