package org.jarb.violation.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jarb.violation.ConstraintViolation;
import org.springframework.util.Assert;

/**
 * Configurable constraint violation exception factory. Enables users
 * to define custom exception factories for a specific constraint.
 * Whenever no custom exception factory could be found, we use a default
 * exception factory. 
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class ConfigurableConstraintViolationExceptionFactory implements ConstraintViolationExceptionFactory {
    private Map<String, ConstraintViolationExceptionFactory> factories;
    private ConstraintViolationExceptionFactory defaultFactory;

    /**
     * Construct a new {@link ConfigurableConstraintViolationExceptionFactory}.
     */
    public ConfigurableConstraintViolationExceptionFactory() {
        factories = new HashMap<String, ConstraintViolationExceptionFactory>();
        defaultFactory = new SimpleConstraintViolationExceptionFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Throwable createException(ConstraintViolation violation) {
        return findFactoryFor(violation).createException(violation);
    }

    /**
     * Retrieve the exception factory for a specific constraint. Whenever no
     * matching factory could we found, we return the default factory.
     * @param violation reference to our constraint violation
     * @return factory capable of building a violation exception for our constraint
     */
    private ConstraintViolationExceptionFactory findFactoryFor(ConstraintViolation violation) {
        // Constraints are always registered in lower case, so we also lookup in lower case
        final String lowerCaseConstraintName = StringUtils.lowerCase(violation.getConstraintName());
        ConstraintViolationExceptionFactory factory = factories.get(lowerCaseConstraintName);
        if (factory == null) {
            factory = defaultFactory;
        }
        return factory;
    }

    /**
     * Register a custom exception factory, for a specific constraint.
     * @param constraintName name of the constraint that our factory applies to, cannot be {@code null}
     * @param factory reference to the factory that should be used
     */
    public void registerFactory(String constraintName, ConstraintViolationExceptionFactory factory) {
        Assert.hasText(constraintName, "Constraint name cannot be empty.");
        // Because each constraint name is stored in lower case, we can find case insensitive
        factories.put(constraintName.toLowerCase(), factory);
    }

    /**
     * Register a custom exception class, for a specific constraint.
     * @param constraintName name of the constraint that our factory applies to, cannot be {@code null}
     * @param exceptionClass class of the exception to generate
     */
    public void registerException(String constraintName, Class<? extends Throwable> exceptionClass) {
        registerFactory(constraintName, new ReflectionConstraintViolationExceptionFactory(exceptionClass));
    }

    /**
     * Register a different default factory. This default factory will be used whenever
     * we cannot find a specific exception factory for our constraint.
     * @param defaultFactory reference to the default factory that should be used, cannot be {@code null}
     */
    public void setDefaultFactory(ConstraintViolationExceptionFactory defaultFactory) {
        Assert.notNull(defaultFactory);
        this.defaultFactory = defaultFactory;
    }

    /**
     * Retrieve the registered constraint violation exception factories.
     * @return unmodifiable map of all registered factories
     */
    public Map<String, ConstraintViolationExceptionFactory> getFactories() {
        return Collections.unmodifiableMap(factories);
    }

}
