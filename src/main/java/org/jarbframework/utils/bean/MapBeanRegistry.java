/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of bean registry.
 *
 * @author Jeroen van Schagen
 * @since Apr 14, 2015
 */
public class MapBeanRegistry implements BeanRegistry {
    
    private final Map<String, Class<?>> bindings = new HashMap<String, Class<?>>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Class<?>> getAll() {
        return new HashSet<Class<?>>(bindings.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getBeanClass(String beanType) {
        final String lowerCasedBeanType = beanType.toLowerCase();
        if (bindings.containsKey(lowerCasedBeanType)) {
            return bindings.get(lowerCasedBeanType);
        } else {
            return getClassForName(beanType);
        }
    }

    private Class<?> getClassForName(String beanType) {
        try {
            return Class.forName(beanType);
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("Could not find bean of type: '" + beanType + "', ensure the type is registered or a full class name is provided.", cnfe);
        }
    }
    
    /**
     * Register a new bean class.
     * 
     * @param beanClass the bean class
     * @return this instance, for chaining
     */
    public BeanRegistry register(Class<?> beanClass) {
        return this.register(beanClass.getSimpleName(), beanClass);
    }
    
    /**
     * Register a new bean type.
     * 
     * @param beanType the bean type
     * @param beanClass the bean class
     * @return this instance, for chaining
     */
    public BeanRegistry register(String beanType, Class<?> beanClass) {
        bindings.put(beanType.toLowerCase(), beanClass);
        return this;
    }
    
}
