package org.jarbframework.constraint.violation.factory.custom;

import static org.jarbframework.utils.Asserts.notNull;

import java.util.ArrayList;
import java.util.List;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.ReflectionConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.TypeBasedConstraintExceptionFactory;

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
    private final List<DatabaseConstraintExceptionFactoryMapping> customFactoryMappings = new ArrayList<DatabaseConstraintExceptionFactoryMapping>();
    
    /** Factory used whenever no custom factory could be determined. **/
    private final DatabaseConstraintExceptionFactory defaultFactory;

    public ConfigurableConstraintExceptionFactory() {
        this(new TypeBasedConstraintExceptionFactory());
    }

    public ConfigurableConstraintExceptionFactory(DatabaseConstraintExceptionFactory defaultFactory) {
        this.defaultFactory = notNull(defaultFactory, "Default violation exception factory cannot be null.");
    }

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
        for (DatabaseConstraintExceptionFactoryMapping customFactoryMapping : customFactoryMappings) {
            if (customFactoryMapping.isSupported(violation)) {
                factory = customFactoryMapping.getExceptionFactory();
                break;
            }
        }
        return factory;
    }

    public ConfigurableConstraintExceptionFactory register(String constraintName, Class<? extends Exception> exceptionClass) {
        return register(constraintName, NameMatchingStrategy.EXACT_IGNORE_CASE, exceptionClass);
    }
    
    public ConfigurableConstraintExceptionFactory register(String constraintName, NameMatchingStrategy nameMatchingStrategy, Class<? extends Exception> exceptionClass) {
        return register(new DatabaseConstraintNameMatcher(constraintName, nameMatchingStrategy), exceptionClass);
    }
    
    public ConfigurableConstraintExceptionFactory register(DatabaseConstraintViolationMatcher violationMatcher, Class<? extends Exception> exceptionClass) {
        return register(violationMatcher, new ReflectionConstraintExceptionFactory(exceptionClass));
    }
    
    /**
     * Register a custom exception factory for specific database constraints.
     * @param violationMatcher describes the constraint(s) that our factory applies to
     * @param exceptionFactory reference to the factory that should be used
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory register(DatabaseConstraintViolationMatcher violationMatcher, DatabaseConstraintExceptionFactory exceptionFactory) {
        return register(new DatabaseConstraintExceptionFactoryMapping(violationMatcher, exceptionFactory));
    }

    /**
     * Register a custom exception factory for specific database constraints.
     * @param exceptionFactoryMapping describes the mapping of an exception factory on constraint violations.
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory register(DatabaseConstraintExceptionFactoryMapping exceptionFactoryMapping) {
        customFactoryMappings.add(exceptionFactoryMapping);
        return this;
    }

}
