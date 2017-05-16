/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint;

import org.jarbframework.constraint.metadata.BeanConstraintDescriptor;
import org.jarbframework.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import org.jarbframework.constraint.violation.resolver.ConfigurableViolationResolver;

/**
 * Support class for enabling database constraints. 
 *
 * @author Jeroen van Schagen
 * @since Feb 12, 2014
 */
public abstract class DatabaseConstraintsConfigurer {
    
    /**
     * Configures the violation resolver.
     * 
     * @param resolver the violation resolver
     */
    public void configureViolationResolver(ConfigurableViolationResolver resolver) {
        // No operation
    }

    /**
     * Configures the constraint exception factory.
     * 
     * @param factory the exception factory
     */
    public void configureExceptionFactory(ConfigurableConstraintExceptionFactory factory) {
        // No operation
    }

    /**
     * Configures the constraint descriptor.
     * 
     * @param descriptor the constraint descriptor
     */
    public void configureConstraintDescriptor(BeanConstraintDescriptor descriptor) {
        // No operation
    }

}
