package org.jarb.violation.integration;

import java.util.Map;

import org.jarb.utils.spring.SingletonFactoryBean;
import org.jarb.violation.ConstraintViolationExceptionTranslator;
import org.jarb.violation.factory.ConfigurableConstraintViolationExceptionFactory;
import org.jarb.violation.factory.ConstraintViolationExceptionFactory;
import org.jarb.violation.resolver.ConstraintViolationResolver;
import org.jarb.violation.resolver.ConstraintViolationResolverFactory;
import org.jarb.violation.resolver.database.DatabaseResolver;

/**
 * Factory bean for {@link ConstraintViolationExceptionTranslator}. Using this factory
 * bean should make the initialization of exception translators significantly easier.
 * 
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
public class ConstraintViolationExceptionTranslatorFactoryBean extends SingletonFactoryBean<ConstraintViolationExceptionTranslator> {
    // Custom exception factories, used for the construction of our translator
    private Map<String, Class<? extends Throwable>> exceptionClasses;
    private Map<String, ConstraintViolationExceptionFactory> exceptionFactories;
    private ConstraintViolationExceptionFactory defaultExceptionFactory;
    // Violation resolver used inside our translator singleton (required)
    private ConstraintViolationResolver violationResolver;

    /**
     * {@inheritDoc}
     */
    @Override
    protected ConstraintViolationExceptionTranslator createObject() throws Exception {
        return new ConstraintViolationExceptionTranslator(violationResolver, createCustomExceptionFactory());
    }

    /**
     * Build the exception factory with all configured custom factories, and exception classes.
     * @return new constraint violation exception factory
     */
    private ConstraintViolationExceptionFactory createCustomExceptionFactory() {
        ConfigurableConstraintViolationExceptionFactory exceptionFactory = new ConfigurableConstraintViolationExceptionFactory();
        if (exceptionClasses != null) {
            for (Map.Entry<String, Class<? extends Throwable>> exceptionClassEntry : exceptionClasses.entrySet()) {
                exceptionFactory.registerException(exceptionClassEntry.getKey(), exceptionClassEntry.getValue());
            }
        }
        // Custom factories are registered last, potentially overwriting custom exception classes
        if (exceptionFactories != null) {
            for (Map.Entry<String, ConstraintViolationExceptionFactory> exceptionFactoryEntry : exceptionFactories.entrySet()) {
                exceptionFactory.registerFactory(exceptionFactoryEntry.getKey(), exceptionFactoryEntry.getValue());
            }
        }
        if (defaultExceptionFactory != null) {
            exceptionFactory.setDefaultFactory(defaultExceptionFactory);
        }
        return exceptionFactory;
    }

    /**
     * Configure a custom violation resolver. The specified resolver will be used
     * inside the generated constraint violation translator.
     * @param violationResolver violation resolver that should be used
     */
    public void setViolationResolver(ConstraintViolationResolver violationResolver) {
        this.violationResolver = violationResolver;
    }

    /**
     * Configure a default violation resolver that uses the provided database resolver.
     * @param databaseResolver determines the database for our violation resolver
     * @see #setViolationResolver(ConstraintViolationResolver)
     */
    public void setDatabaseResolver(DatabaseResolver databaseResolver) {
        setViolationResolver(ConstraintViolationResolverFactory.build(databaseResolver));
    }

    /**
     * Configure the custom exception classes for specific named constraints.
     * @param exceptionClasses mapping of exception classes for named constraints
     */
    public void setExceptionClasses(Map<String, Class<? extends Throwable>> exceptionClasses) {
        this.exceptionClasses = exceptionClasses;
    }

    /**
     * Configure the custom exception factories for specific named constraints.
     * @param exceptionFactories mapping of exception factories for named constraints
     */
    public void setExceptionFactories(Map<String, ConstraintViolationExceptionFactory> exceptionFactories) {
        this.exceptionFactories = exceptionFactories;
    }

    /**
     * Configure the exception factory that should be used for violations without
     * a custom exception factory.
     * @param defaultExceptionFactory default exception factory
     */
    public void setDefaultExceptionFactory(ConstraintViolationExceptionFactory defaultExceptionFactory) {
        this.defaultExceptionFactory = defaultExceptionFactory;
    }

}
