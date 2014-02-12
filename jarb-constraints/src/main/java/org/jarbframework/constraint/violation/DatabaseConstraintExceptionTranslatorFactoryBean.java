/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.violation;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.jarbframework.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.DefaultConstraintExceptionFactory;
import org.jarbframework.constraint.violation.resolver.ConfigurableViolationResolver;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.utils.orm.hibernate.HibernateUtils;
import org.jarbframework.utils.spring.SingletonFactoryBean;

/**
 * Create an exception translator based on a certain package and data source.
 * 
 * @author Jeroen van Schagen
 */
public final class DatabaseConstraintExceptionTranslatorFactoryBean extends SingletonFactoryBean<DatabaseConstraintExceptionTranslator> {
	
    private String basePackage;
	
    private DataSource dataSource;

    private DatabaseConstraintExceptionFactory defaultExceptionFactory = new DefaultConstraintExceptionFactory();
    
    @Override
    protected DatabaseConstraintExceptionTranslator createObject() throws Exception {
        DatabaseConstraintViolationResolver violationResolver = new ConfigurableViolationResolver(dataSource, basePackage);
        DatabaseConstraintExceptionFactory exceptionFactory = new ConfigurableConstraintExceptionFactory(defaultExceptionFactory).registerAll(basePackage);
        return new DatabaseConstraintExceptionTranslator(violationResolver, exceptionFactory);
    }
    
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        setDataSource(HibernateUtils.getDataSource(entityManagerFactory));
    }

    public void setDefaultExceptionFactory(DatabaseConstraintExceptionFactory defaultExceptionFactory) {
        this.defaultExceptionFactory = defaultExceptionFactory;
    }

}