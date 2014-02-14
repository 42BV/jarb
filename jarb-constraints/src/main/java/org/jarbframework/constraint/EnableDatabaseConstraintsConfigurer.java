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
public abstract class EnableDatabaseConstraintsConfigurer {
    
    public void addViolationResolvers(ConfigurableViolationResolver resolver) {
        // No operation
    }

    public void addConstraintExceptions(ConfigurableConstraintExceptionFactory factory) {
        // No operation
    }

    public void addPropertyEnhancers(BeanConstraintDescriptor descriptor) {
        // No operation
    }

}
