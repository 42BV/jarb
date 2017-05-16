/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.bean;

import java.util.Set;

/**
 * Retrieves the bean class based on a type name.
 *
 * @author Jeroen van Schagen
 * @since Apr 14, 2015
 */
public interface BeanRegistry {
    
    /**
     * Retrieves the bean class based on a type name.
     * 
     * @param beanType the type name
     * @return the bean class
     */
    Class<?> getBeanClass(String beanType);
    
    /**
     * Retrieves all bean classes.
     * 
     * @return the bean classes
     */
    Set<Class<?>> getAll();
    
}