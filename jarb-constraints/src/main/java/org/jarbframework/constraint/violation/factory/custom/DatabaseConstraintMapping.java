package org.jarbframework.constraint.violation.factory.custom;

// TODO: Allow annotation on types and create component scanning factory
public @interface DatabaseConstraintMapping {

    String name();
    
    DatabaseConstraintMappingStrategy strategy();
    
}
