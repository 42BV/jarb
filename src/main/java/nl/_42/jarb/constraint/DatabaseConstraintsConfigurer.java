/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint;

import nl._42.jarb.constraint.metadata.BeanConstraintDescriptor;
import nl._42.jarb.constraint.validation.DatabaseConstraintValidator;
import nl._42.jarb.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.resolver.ConfigurableViolationResolver;

import nl._42.jarb.constraint.metadata.BeanConstraintDescriptor;
import nl._42.jarb.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.resolver.ConfigurableViolationResolver;

/**
 * Support class for enabling database constraints. 
 *
 * @author Jeroen van Schagen
 * @since Feb 12, 2014
 */
public interface DatabaseConstraintsConfigurer {

    /**
     * Configures the validation steps.
     *
     * @param validator the validator
     */
    default void configureValidator(DatabaseConstraintValidator validator) {
        // No operation
    }
    
    /**
     * Configures the violation resolver.
     * 
     * @param resolver the violation resolver
     */
    default void configureViolationResolver(ConfigurableViolationResolver resolver) {
        // No operation
    }

    /**
     * Configures the constraint exception factory.
     * 
     * @param factory the exception factory
     */
    default void configureExceptionFactory(ConfigurableConstraintExceptionFactory factory) {
        // No operation
    }

    /**
     * Configures the constraint descriptor.
     * 
     * @param descriptor the constraint descriptor
     */
    default void configureConstraintDescriptor(BeanConstraintDescriptor descriptor) {
        // No operation
    }

}
