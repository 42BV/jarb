package org.jarbframework.violation.configuration.xml;

import java.util.Collection;

import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.jarbframework.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.violation.factory.custom.ConfigurableConstraintExceptionFactory;
import org.jarbframework.violation.factory.custom.ExceptionFactoryMapping;
import org.springframework.beans.factory.annotation.Required;

class ConfigurableConstraintExceptionFactoryParserFactoryBean extends SingletonFactoryBean<ConfigurableConstraintExceptionFactory> {
    private Collection<ExceptionFactoryMapping> customFactoryMappings;
    private DatabaseConstraintExceptionFactory defaultFactory;
    
    @Required
    public void setCustomFactoryMappings(Collection<ExceptionFactoryMapping> customFactoryMappings) {
        this.customFactoryMappings = customFactoryMappings;
    }
    
    public void setDefaultFactory(DatabaseConstraintExceptionFactory defaultFactory) {
        this.defaultFactory = defaultFactory;
    }

    @Override
    protected ConfigurableConstraintExceptionFactory createObject() throws Exception {
        ConfigurableConstraintExceptionFactory configurableFactory;
        if(defaultFactory != null) {
            configurableFactory = new ConfigurableConstraintExceptionFactory(defaultFactory);
        } else {
            configurableFactory = new ConfigurableConstraintExceptionFactory();
        }
        for(ExceptionFactoryMapping customFactoryMapping : customFactoryMappings) {
            configurableFactory.registerMapping(customFactoryMapping);
        }
        return configurableFactory;
    }
    
}
