/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint.violation;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import nl._42.jarb.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.factory.DefaultConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.resolver.ConfigurableViolationResolver;
import nl._42.jarb.constraint.violation.resolver.DatabaseConstraintViolationResolver;

import nl._42.jarb.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.factory.DefaultConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.resolver.ConfigurableViolationResolver;
import nl._42.jarb.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import nl._42.jarb.utils.SingletonFactoryBean;
import nl._42.jarb.utils.orm.hibernate.HibernateUtils;

/**
 * Create an exception translator based on a certain package and data source.
 * 
 * @author Jeroen van Schagen
 */
public final class DatabaseConstraintExceptionTranslatorFactoryBean extends SingletonFactoryBean<DatabaseConstraintExceptionTranslator> {
	
    private final DataSource dataSource;

    private String basePackage;
	
    private DatabaseConstraintExceptionFactory defaultExceptionFactory;

    public DatabaseConstraintExceptionTranslatorFactoryBean(DataSource dataSource) {
        this.dataSource = dataSource;
        defaultExceptionFactory = new DefaultConstraintExceptionFactory();
    }
    
    public DatabaseConstraintExceptionTranslatorFactoryBean(EntityManagerFactory entityManagerFactory) {
        this(HibernateUtils.getDataSource(entityManagerFactory));
    }
    
    @Override
    protected DatabaseConstraintExceptionTranslator createObject() throws Exception {
        DatabaseConstraintViolationResolver violationResolver = new ConfigurableViolationResolver(dataSource, basePackage);
        DatabaseConstraintExceptionFactory exceptionFactory = new ConfigurableConstraintExceptionFactory(defaultExceptionFactory).registerAll(basePackage);
        return new DatabaseConstraintExceptionTranslator(violationResolver, exceptionFactory);
    }
    
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setDefaultExceptionFactory(DatabaseConstraintExceptionFactory defaultExceptionFactory) {
        this.defaultExceptionFactory = defaultExceptionFactory;
    }

}