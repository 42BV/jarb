package org.jarbframework.violation.configuration;

import java.util.Map;

import javax.sql.DataSource;

import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.jarbframework.violation.DatabaseConstraintExceptionTranslator;
import org.jarbframework.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.violation.factory.custom.ConfigurableConstraintExceptionFactory;
import org.jarbframework.violation.factory.custom.ConstraintViolationMatcher;
import org.jarbframework.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.violation.resolver.DatabaseConstraintViolationResolverFactory;

/**
 * Factory bean for {@link DatabaseConstraintExceptionTranslator}.
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
public class DatabaseConstraintExceptionTranslatorFactoryBean extends SingletonFactoryBean<DatabaseConstraintExceptionTranslator> {
    /** Mapping of custom exception factories **/
    private Map<ConstraintViolationMatcher, DatabaseConstraintExceptionFactory> customExceptionFactories;
    /** Default exception factory **/
    private DatabaseConstraintExceptionFactory defaultExceptionFactory;

    /** Data source from which exceptions are retrieved **/
    private DataSource dataSource;

    /**
     * {@inheritDoc}
     */
    @Override
    protected DatabaseConstraintExceptionTranslator createObject() throws Exception {
        return new DatabaseConstraintExceptionTranslator(buildViolationResolver(), buildExceptionFactory());
    }

    private DatabaseConstraintViolationResolver buildViolationResolver() {
        return new DatabaseConstraintViolationResolverFactory().build(dataSource);
    }
    
    private DatabaseConstraintExceptionFactory buildExceptionFactory() {
        ConfigurableConstraintExceptionFactory exceptionFactory = new ConfigurableConstraintExceptionFactory();
        if (customExceptionFactories != null) {
            for (Map.Entry<ConstraintViolationMatcher, DatabaseConstraintExceptionFactory> factoriesEntry : customExceptionFactories.entrySet()) {
                exceptionFactory.registerFactory(factoriesEntry.getKey(), factoriesEntry.getValue());
            }
        }
        if (defaultExceptionFactory != null) {
            exceptionFactory.setDefaultFactory(defaultExceptionFactory);
        }
        return exceptionFactory;
    }

    public void setCustomExceptionFactories(Map<ConstraintViolationMatcher, DatabaseConstraintExceptionFactory> customExceptionFactories) {
        this.customExceptionFactories = customExceptionFactories;
    }

    public void setDefaultExceptionFactory(DatabaseConstraintExceptionFactory defaultExceptionFactory) {
        this.defaultExceptionFactory = defaultExceptionFactory;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
}
