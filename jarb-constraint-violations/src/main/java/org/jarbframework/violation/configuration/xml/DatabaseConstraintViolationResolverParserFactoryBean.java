package org.jarbframework.violation.configuration.xml;

import javax.sql.DataSource;

import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.jarbframework.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.violation.resolver.DatabaseConstraintViolationResolverFactory;
import org.springframework.beans.factory.annotation.Required;

class DatabaseConstraintViolationResolverParserFactoryBean extends SingletonFactoryBean<DatabaseConstraintViolationResolver> {
    private DataSource dataSource;
    
    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected DatabaseConstraintViolationResolver createObject() throws Exception {
        return new DatabaseConstraintViolationResolverFactory().build(dataSource);
    }
}
