/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.violation;

import javax.sql.DataSource;

import org.jarbframework.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.constraint.violation.resolver.DefaultViolationResolver;
import org.jarbframework.utils.spring.SingletonFactoryBean;

/**
 * Create an exception translator based on a certain package and data source.
 * 
 * @author Jeroen van Schagen
 */
public final class DatabaseConstraintExceptionTranslatorFactoryBean extends SingletonFactoryBean<DatabaseConstraintExceptionTranslator> {
	
    private final String basePackage;
	
    private final DataSource dataSource;

    private final DatabaseConstraintExceptionFactory defaultFactory;
    
    public DatabaseConstraintExceptionTranslatorFactoryBean(String basePackage, DataSource dataSource) {
        this(basePackage, dataSource, null);
    }
    
    public DatabaseConstraintExceptionTranslatorFactoryBean(String basePackage, DataSource dataSource, DatabaseConstraintExceptionFactory defaultFactory) {
        this.basePackage = basePackage;
        this.dataSource = dataSource;
        this.defaultFactory = defaultFactory;
    }

    @Override
    protected DatabaseConstraintExceptionTranslator createObject() throws Exception {
        DatabaseConstraintViolationResolver violationResolver = new DefaultViolationResolver(dataSource, basePackage);
        DatabaseConstraintExceptionFactory exceptionFactory = new ConfigurableConstraintExceptionFactory(defaultFactory).registerAll(basePackage);
        return new DatabaseConstraintExceptionTranslator(violationResolver, exceptionFactory);
    }

}