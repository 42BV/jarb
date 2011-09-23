package org.jarbframework.violation.factory.custom;

import static org.jarbframework.utils.Asserts.notNull;
import static org.jarbframework.violation.factory.custom.ConstraintNameMatcher.exact;

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
    private final List<ExceptionFactoryMapping> customFactoryMappings = new ArrayList<ExceptionFactoryMapping>();
    /** Factory used whenever no custom factory could be determined. **/
    private final DatabaseConstraintExceptionFactory defaultFactory;

    /**
     * Construct a new {@link ConfigurableConstraintExceptionFactory}.
     */
    public ConfigurableConstraintExceptionFactory() {
        this(new SimpleConstraintExceptionFactory());
    }
    
    public ConfigurableConstraintExceptionFactory(DatabaseConstraintExceptionFactory defaultFactory) {
        this.defaultFactory = notNull(defaultFactory, "Default violation exception factory cannot be null.");
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
        for (ExceptionFactoryMapping customFactoryMapping : customFactoryMappings) {
            if (customFactoryMapping.supports(violation)) {
                factory = customFactoryMapping.getExceptionFactory();
                break;
            }
        }
        return factory;
    }
    
    /**
     * Register a custom exception class for specific database constraints.
     * @param constraintName name of the constraint that our factory applies to
     * @param exceptionClass class of the exception that should be thrown
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory registerException(String constraintName, Class<? extends Throwable> exceptionClass) {
        return registerException(exact(constraintName), exceptionClass);
    }

    /**
     * Register a custom exception class for specific database constraints.
     * @param violationMatcher describes the constraint(s) that our factory applies to
     * @param exceptionClass class of the exception that should be thrown
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory registerException(ConstraintViolationMatcher violationMatcher, Class<? extends Throwable> exceptionClass) {
        return registerFactory(violationMatcher, new ReflectionConstraintExceptionFactory(exceptionClass));
    }
    
    /**
     * Register a custom exception factory for specific database constraints.
     * @param constraintName name of the constraint that our factory applies to
     * @param exceptionFactory reference to the factory that should be used
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory registerFactory(String constraintName, DatabaseConstraintExceptionFactory exceptionFactory) {
        return registerFactory(exact(constraintName), exceptionFactory);
    }

    /**
     * Register a custom exception factory for specific database constraints.
     * @param violationMatcher describes the constraint(s) that our factory applies to
     * @param exceptionFactory reference to the factory that should be used
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory registerFactory(ConstraintViolationMatcher violationMatcher, DatabaseConstraintExceptionFactory exceptionFactory) {
        return registerMapping(new ExceptionFactoryMapping(violationMatcher, exceptionFactory));
    }
    
    /**
     * Register a custom exception factory for specific database constraints.
     * @param exceptionFactoryMapping describes the mapping of an exception factory on constraint violations.
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory registerMapping(ExceptionFactoryMapping exceptionFactoryMapping) {
        customFactoryMappings.add(notNull(exceptionFactoryMapping, "Exception factory mapping cannot be null."));
        return this;
    }

}
