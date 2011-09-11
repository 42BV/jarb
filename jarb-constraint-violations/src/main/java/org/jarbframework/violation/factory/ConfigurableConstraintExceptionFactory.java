package org.jarbframework.violation.factory;

import static org.jarbframework.utils.Conditions.notNull;

import java.util.HashMap;
import java.util.Map;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.factory.mapping.ConstraintViolationMatcher;

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
    private Map<ConstraintViolationMatcher, DatabaseConstraintExceptionFactory> customFactories;
    /** Factory used whenever no custom factory could be determined. **/
    private DatabaseConstraintExceptionFactory defaultFactory;

    /**
     * Construct a new {@link ConfigurableConstraintExceptionFactory}.
     */
    public ConfigurableConstraintExceptionFactory() {
        customFactories = new HashMap<ConstraintViolationMatcher, DatabaseConstraintExceptionFactory>();
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
        for (Map.Entry<ConstraintViolationMatcher, DatabaseConstraintExceptionFactory> customFactoryEntry : customFactories.entrySet()) {
            if (customFactoryEntry.getKey().matches(violation)) {
                factory = customFactoryEntry.getValue();
                break;
            }
        }
        return factory;
    }
    
    /**
     * Register a custom exception class for a specific constraint.
     * @param expression name of the constraint that our factory applies to
     * @param exceptionClass class of the exception that should be thrown
     */
    public void registerException(String expression, Class<? extends Throwable> exceptionClass) {
        registerException(new ConstraintViolationMatcher(expression), exceptionClass);
    }

    /**
     * Register a custom exception class for a specific constraint.
     * @param matcher describes the constraint(s) that our factory applies to
     * @param exceptionClass class of the exception that should be thrown
     */
    public void registerException(ConstraintViolationMatcher matcher, Class<? extends Throwable> exceptionClass) {
        registerFactory(matcher, new ReflectionConstraintExceptionFactory(exceptionClass));
    }

    /**
     * Register a custom exception factory for a specific constraint.
     * @param expression name of the constraint that our factory applies to
     * @param factory reference to the factory that should be used
     */
    public void registerFactory(String expression, DatabaseConstraintExceptionFactory factory) {
        registerFactory(new ConstraintViolationMatcher(expression), factory);
    }

    /**
     * Register a custom exception factory for a specific constraint.
     * @param matcher describes the constraint(s) that our factory applies to
     * @param factory reference to the factory that should be used
     */
    public void registerFactory(ConstraintViolationMatcher matcher, DatabaseConstraintExceptionFactory factory) {
        customFactories.put(matcher, factory);
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
