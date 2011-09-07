package org.jarbframework.utils.spring;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.jarbframework.utils.Conditions.notNull;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

public class BeanFactoryScanner {
    private final BeanFactory beanFactory;
    
    public BeanFactoryScanner(BeanFactory beanFactory) {
        this.beanFactory = notNull(beanFactory, "Bean factory cannot be null.");
    }

    public <T> T findBean(Class<T> beanClass, String identifier) {
        return findBean(beanClass, identifier, null);
    }
    
    public <T> T findBean(Class<T> beanClass, String identifier, String defaultIdentifier) {
        notNull(beanClass, "Bean class cannot be null");
        
        T foundBean;
        if (isBlank(identifier)) {
            // Whenever no identifier is specified, look if the bean is unique
            try {
                foundBean = beanFactory.getBean(beanClass);
            } catch (NoSuchBeanDefinitionException noSuchBeanException) {
                // Whenever we could not find a matching unique bean, attempt the default identifier
                if(isNotBlank(defaultIdentifier)) {
                    foundBean = beanFactory.getBean(defaultIdentifier, beanClass);
                } else {
                    throw noSuchBeanException;
                }
            }
        } else {
            // Find the bean based on its specified non-blank identifier
            foundBean = beanFactory.getBean(identifier, beanClass);
        }
        return foundBean;
    }
    
}
