package org.jarbframework.violation.configuration;

import static org.jarbframework.violation.factory.custom.ConstraintViolationMatcher.regex;

import java.util.Collections;
import java.util.Map;

import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.jarbframework.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.violation.factory.custom.ConfigurableConstraintExceptionFactory;

public class ConfigurableConstraintExceptionFactoryFactoryBean extends SingletonFactoryBean<ConfigurableConstraintExceptionFactory> {
    private Map<String, Class<? extends Exception>> customExceptionClasses = Collections.emptyMap();
    private DatabaseConstraintExceptionFactory defaultFactory;
    
    public void setCustomExceptionClasses(Map<String, Class<? extends Exception>> customExceptionClasses) {
        this.customExceptionClasses = customExceptionClasses;
    }
    
    public void setDefaultFactory(DatabaseConstraintExceptionFactory defaultFactory) {
        this.defaultFactory = defaultFactory;
    }

    @Override
    protected ConfigurableConstraintExceptionFactory createObject() throws Exception {
        ConfigurableConstraintExceptionFactory configurableFactory = new ConfigurableConstraintExceptionFactory();
        for(Map.Entry<String, Class<? extends Exception>> customExceptionClass : customExceptionClasses.entrySet()) {
            configurableFactory.registerException(regex(customExceptionClass.getKey()), customExceptionClass.getValue());
        }
        if(defaultFactory != null) {
            configurableFactory.setDefaultFactory(defaultFactory);
        }
        return configurableFactory;
    }
    
}
