package org.jarbframework.constraint.metadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes the constraints of a baen.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 *
 * @param <T> type of bean being described
 */
public class BeanConstraintDescription {

    private final Class<?> beanClass;

    /** Provides a description of all property constraints. **/
    private final Map<String, PropertyConstraintDescription> properties = new HashMap<String, PropertyConstraintDescription>();

    /**
     * Construct a new {@link BeanConstraintDescription}.
     * 
     * @param beanClass class of the bean being described
     */
    public BeanConstraintDescription(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getJavaType() {
        return beanClass;
    }

    /**
     * Retrieve a specific property.
     * 
     * @param propertyName the property name
     * @return the constraint description
     */
    public PropertyConstraintDescription getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    /**
     * Retrieve all properties.
     * 
     * @return the properties
     */
    public Map<String, PropertyConstraintDescription> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Attach the description of a property to this bean description.
     * 
     * @param property description of the property constraints
     */
    public void addProperty(PropertyConstraintDescription property) {
        properties.put(property.getName(), property);
    }

}
