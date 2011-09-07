package org.jarb.violation.integration;

import java.util.Map;

import org.jarb.utils.spring.SingletonFactoryBean;
import org.jarb.violation.DatabaseConstraintExceptionTranslator;
import org.jarb.violation.factory.ConfigurableViolationExceptionFactory;
import org.jarb.violation.factory.DatabaseConstraintViolationExceptionFactory;
import org.jarb.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarb.violation.resolver.DatabaseConstraintViolationResolverFactory;
import org.jarb.violation.resolver.database.DatabaseTypeResolver;
import org.springframework.util.Assert;

/**
 * Factory bean for {@link DatabaseConstraintExceptionTranslator}. Using this factory
 * bean should make the initialization of exception translators significantly easier.
 * 
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
public class ConstraintViolationExceptionTranslatorFactoryBean extends SingletonFactoryBean<DatabaseConstraintExceptionTranslator> {
    // Custom exception factories, used for the construction of our translator
    private Map<String, Class<? extends Throwable>> exceptionClasses;
    private Map<String, DatabaseConstraintViolationExceptionFactory> exceptionFactories;
    private DatabaseConstraintViolationExceptionFactory defaultExceptionFactory;
    // Violation resolver used inside our translator singleton (required)
    private DatabaseConstraintViolationResolver violationResolver;

    /**
     * {@inheritDoc}
     */
    @Override
    protected DatabaseConstraintExceptionTranslator createObject() throws Exception {
        return new DatabaseConstraintExceptionTranslator(violationResolver, createCustomExceptionFactory());
    }

    /**
     * Build the exception factory with all configured custom factories, and exception classes.
     * @return new constraint violation exception factory
     */
    private DatabaseConstraintViolationExceptionFactory createCustomExceptionFactory() {
        ConfigurableViolationExceptionFactory exceptionFactory = new ConfigurableViolationExceptionFactory();
        if (exceptionClasses != null) {
            for (Map.Entry<String, Class<? extends Throwable>> exceptionClassEntry : exceptionClasses.entrySet()) {
                exceptionFactory.registerException(exceptionClassEntry.getKey(), exceptionClassEntry.getValue());
            }
        }
        // Custom factories are registered last, potentially overwriting custom exception classes
        if (exceptionFactories != null) {
            for (Map.Entry<String, DatabaseConstraintViolationExceptionFactory> exceptionFactoryEntry : exceptionFactories.entrySet()) {
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
    public void setViolationResolver(DatabaseConstraintViolationResolver violationResolver) {
        this.violationResolver = violationResolver;
    }

    /**
     * Configure a default violation resolver that uses the provided database resolver.
     * @param databaseResolver determines the database for our violation resolver
     * @see #setViolationResolver(DatabaseConstraintViolationResolver)
     */
    public void setDatabaseResolver(DatabaseTypeResolver databaseResolver) {
        Assert.notNull(databaseResolver, "Database resolver cannot be null.");
        setViolationResolver(DatabaseConstraintViolationResolverFactory.build(databaseResolver));
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
    public void setExceptionFactories(Map<String, DatabaseConstraintViolationExceptionFactory> exceptionFactories) {
        this.exceptionFactories = exceptionFactories;
    }

    /**
     * Configure the exception factory that should be used for violations without
     * a custom exception factory.
     * @param defaultExceptionFactory default exception factory
     */
    public void setDefaultExceptionFactory(DatabaseConstraintViolationExceptionFactory defaultExceptionFactory) {
        this.defaultExceptionFactory = defaultExceptionFactory;
    }

}
