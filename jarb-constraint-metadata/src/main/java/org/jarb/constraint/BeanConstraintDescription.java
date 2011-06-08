package org.jarb.constraint;

import java.util.Collection;

/**
 * Describes the constraints of a bean.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 *
 * @param <T> type of bean
 */
public interface BeanConstraintDescription<T> {

    /**
     * Retrieve the bean type.
     * @return bean type
     */
    Class<T> getBeanType();
    
    /**
     * Retrieve the description of a property.
     * @param propertyName property name
     * @return property description
     */
    PropertyConstraintDescription<?> getPropertyDescription(String propertyName);
    
    /**
     * Retrieve the description of a property.
     * @param <X> type of property
     * @param propertyName property name
     * @param propertyClass class of the property
     * @return type safe property description
     */
    <X> PropertyConstraintDescription<X> getPropertyDescription(String propertyName, Class<X> propertyClass);
        
    /**
     * Retrieve all property descriptions.
     * @return immutable collection of all property descriptions
     */
    Collection<PropertyConstraintDescription<?>> getPropertyDescriptions();
    
}
