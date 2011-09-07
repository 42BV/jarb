package org.jarbframework.violation.integration;

import java.util.Map;

import javax.sql.DataSource;

import org.jarbframework.utils.database.JdbcMetadataDatabaseTypeResolver;
import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.jarbframework.violation.DatabaseConstraintExceptionTranslator;
import org.jarbframework.violation.factory.ConfigurableViolationExceptionFactory;
import org.jarbframework.violation.factory.DatabaseConstraintViolationExceptionFactory;
import org.jarbframework.violation.resolver.DatabaseConstraintViolationResolverFactory;

/**
 * Factory bean for {@link DatabaseConstraintExceptionTranslator}. Using this factory
 * bean should make the initialization of exception translators significantly easier.
 * 
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
public class ConstraintViolationExceptionTranslatorFactoryBean extends SingletonFactoryBean<DatabaseConstraintExceptionTranslator> {
    /** Mapping of custom exception classes **/
    private Map<String, Class<? extends Throwable>> exceptionClasses;
    /** Mapping of custom exception factories **/
    private Map<String, DatabaseConstraintViolationExceptionFactory> exceptionFactories;
    /** Default exception factory **/
    private DatabaseConstraintViolationExceptionFactory defaultExceptionFactory;

    /** Data source from which exceptions are retrieved **/
    private DataSource dataSource;

    /**
     * {@inheritDoc}
     */
    @Override
    protected DatabaseConstraintExceptionTranslator createObject() throws Exception {
        DatabaseConstraintViolationResolverFactory violationResolverFactory = new DatabaseConstraintViolationResolverFactory();
        violationResolverFactory.setDatabaseTypeResolver(new JdbcMetadataDatabaseTypeResolver());
        return new DatabaseConstraintExceptionTranslator(violationResolverFactory.build(dataSource), createCustomExceptionFactory());
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

    public void setExceptionClasses(Map<String, Class<? extends Throwable>> exceptionClasses) {
        this.exceptionClasses = exceptionClasses;
    }

    public void setExceptionFactories(Map<String, DatabaseConstraintViolationExceptionFactory> exceptionFactories) {
        this.exceptionFactories = exceptionFactories;
    }

    public void setDefaultExceptionFactory(DatabaseConstraintViolationExceptionFactory defaultExceptionFactory) {
        this.defaultExceptionFactory = defaultExceptionFactory;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
}
