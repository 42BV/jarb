package org.jarbframework.violation.factory.custom;

import static org.jarbframework.utils.Asserts.notNull;
import static org.jarbframework.violation.factory.custom.ConstraintViolationMatcher.name;

import java.util.ArrayList;
import java.util.List;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.violation.factory.ReflectionConstraintExceptionFactory;
import org.jarbframework.violation.factory.SimpleConstraintExceptionFactory;

/**
 * Configurable constraint violation exception factory. Enables users
 * to define custom exception factories for a specific constraint.
 * Whenever no custom exception factory could be found, we use a default
 * exception factory. 
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class ConfigurableConstraintExceptionFactory implements DatabaseConstraintExceptionFactory {
    /** Registered custom exception factories. **/
    private List<CustomExceptionMapping> customFactoryMappings;
    /** Factory used whenever no custom factory could be determined. **/
    private DatabaseConstraintExceptionFactory defaultFactory;

    /**
     * Construct a new {@link ConfigurableConstraintExceptionFactory}.
     */
    public ConfigurableConstraintExceptionFactory() {
        customFactoryMappings = new ArrayList<CustomExceptionMapping>();
        defaultFactory = new SimpleConstraintExceptionFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Throwable createException(DatabaseConstraintViolation violation, Throwable cause) {
        notNull(violation, "Cannot create exception for a null violation.");
        return findFactoryFor(violation).createException(violation, cause);
    }

    /**
     * Retrieve the exception factory for a specific constraint. Whenever no
     * matching factory could we found, we return the default factory.
     * @param violation reference to our constraint violation
     * @return factory capable of building a violation exception for our constraint
     */
    private DatabaseConstraintExceptionFactory findFactoryFor(DatabaseConstraintViolation violation) {
        DatabaseConstraintExceptionFactory factory = defaultFactory;
        for (CustomExceptionMapping customFactoryMapping : customFactoryMappings) {
            if (customFactoryMapping.supports(violation)) {
                factory = customFactoryMapping.getExceptionFactory();
                break;
            }
        }
        return factory;
    }
    
    /**
     * Register a custom exception class for a specific constraint.
     * @param constraintName name of the constraint that our factory applies to
     * @param exceptionClass class of the exception that should be thrown
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory registerException(String constraintName, Class<? extends Throwable> exceptionClass) {
        return registerException(name(constraintName), exceptionClass);
    }

    /**
     * Register a custom exception class for a specific constraint.
     * @param violationMatcher describes the constraint(s) that our factory applies to
     * @param exceptionClass class of the exception that should be thrown
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory registerException(ConstraintViolationMatcher violationMatcher, Class<? extends Throwable> exceptionClass) {
        return registerFactory(violationMatcher, new ReflectionConstraintExceptionFactory(exceptionClass));
    }
    
    /**
     * Register a custom exception factory for a specific constraint.
     * @param constraintName name of the constraint that our factory applies to
     * @param exceptionFactory reference to the factory that should be used
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory registerFactory(String constraintName, DatabaseConstraintExceptionFactory exceptionFactory) {
        return registerFactory(name(constraintName), exceptionFactory);
    }

    /**
     * Register a custom exception factory for a specific constraint.
     * @param violationMatcher describes the constraint(s) that our factory applies to
     * @param exceptionFactory reference to the factory that should be used
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory registerFactory(ConstraintViolationMatcher violationMatcher, DatabaseConstraintExceptionFactory exceptionFactory) {
        customFactoryMappings.add(new CustomExceptionMapping(violationMatcher, exceptionFactory));
        return this;
    }

    /**
     * Register a different default factory. This default factory will be used whenever
     * we cannot find a specific exception factory for our constraint.
     * @param defaultFactory reference to the default factory that should be used, cannot be {@code null}
     */
    public void setDefaultFactory(DatabaseConstraintExceptionFactory defaultFactory) {
        this.defaultFactory = notNull(defaultFactory, "Default violation exception factory cannot be null.");
    }

}
