package org.jarbframework.constraint.metadata;

import java.util.Collection;
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
    private final Map<String, PropertyConstraintDescription> properties = new HashMap<>();

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

    public PropertyConstraintDescription getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public Collection<PropertyConstraintDescription> getProperty() {
        return properties.values();
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
