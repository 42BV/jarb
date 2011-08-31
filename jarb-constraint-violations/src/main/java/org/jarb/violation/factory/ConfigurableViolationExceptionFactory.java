package org.jarb.violation.factory;

import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.jarb.utils.Conditions.hasText;
import static org.jarb.utils.Conditions.notNull;

import java.util.HashMap;
import java.util.Map;

import org.jarb.violation.DatabaseConstraintViolation;

/**
 * Configurable constraint violation exception factory. Enables users
 * to define custom exception factories for a specific constraint.
 * Whenever no custom exception factory could be found, we use a default
 * exception factory. 
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class ConfigurableViolationExceptionFactory implements DatabaseConstraintViolationExceptionFactory {
    /** Registered custom exception factories. **/
    private Map<String, DatabaseConstraintViolationExceptionFactory> factories;
    /** Factory used whenever no custom factory could be determined. **/
    private DatabaseConstraintViolationExceptionFactory defaultFactory;
    /** Matches the constraint names against registered expressions. **/
    private ConstraintExpressionMatcher constraintMatcher;

    /**
     * Construct a new {@link ConfigurableViolationExceptionFactory}.
     */
    public ConfigurableViolationExceptionFactory() {
        factories = new HashMap<String, DatabaseConstraintViolationExceptionFactory>();
        defaultFactory = new DefaultViolationExceptionFactory();
        constraintMatcher = new RegexConstraintExpressionMatcher();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Throwable createException(DatabaseConstraintViolation violation, Throwable cause) {
        return findFactoryFor(notNull(violation, "Cannot create exception for a null violation.")).createException(violation, cause);
    }

    /**
     * Retrieve the exception factory for a specific constraint. Whenever no
     * matching factory could we found, we return the default factory.
     * @param violation reference to our constraint violation
     * @return factory capable of building a violation exception for our constraint
     */
    private DatabaseConstraintViolationExceptionFactory findFactoryFor(DatabaseConstraintViolation violation) {
        DatabaseConstraintViolationExceptionFactory factory = null;
        // Attempt to find a custom factory that matches the constraint name
        String constraintName = lowerCase(violation.getConstraintName());
        for(String constraintExpression : factories.keySet()) {
            if(constraintMatcher.matches(constraintName, constraintExpression)) {
                factory = factories.get(constraintExpression);
            }
        }
        // Otherwise use the default factory
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
    public void registerFactory(String constraintName, DatabaseConstraintViolationExceptionFactory factory) {
        factories.put(hasText(constraintName, "Constraint name cannot be empty.").toLowerCase(), factory);
    }

    /**
     * Register a custom exception class, for a specific constraint.
     * @param constraintName name of the constraint that our factory applies to, cannot be {@code null}
     * @param exceptionClass class of the exception to generate
     */
    public void registerException(String constraintName, Class<? extends Throwable> exceptionClass) {
        registerFactory(constraintName, new ReflectionViolationExceptionFactory(exceptionClass));
    }

    /**
     * Register a different default factory. This default factory will be used whenever
     * we cannot find a specific exception factory for our constraint.
     * @param defaultFactory reference to the default factory that should be used, cannot be {@code null}
     */
    public void setDefaultFactory(DatabaseConstraintViolationExceptionFactory defaultFactory) {
        this.defaultFactory = notNull(defaultFactory, "Default violation exception factory cannot be null.");
    }

    /**
     * Retrieve the registered constraint violation exception factories.
     * @return unmodifiable map of all registered factories
     */
    public Map<String, DatabaseConstraintViolationExceptionFactory> getFactories() {
        return unmodifiableMap(factories);
    }

}
