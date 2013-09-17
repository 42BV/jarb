package org.jarbframework.constraint.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Describes the constraints of a baen.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 *
 * @param <T> type of bean being described
 */
public class BeanConstraintDescription {

    /** Provides a description of all property constraints. **/
    private final Map<String, PropertyConstraintDescription> propertyDescriptions = new HashMap<String, PropertyConstraintDescription>();

    private final Class<?> beanClass;

    /**
     * Construct a new {@link BeanConstraintDescription}.
     * @param beanClass class of the bean being described
     */
    public BeanConstraintDescription(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getJavaType() {
        return beanClass;
    }

    public PropertyConstraintDescription getPropertyDescription(String propertyName) {
        return propertyDescriptions.get(propertyName);
    }

    public Collection<PropertyConstraintDescription> getPropertyDescriptions() {
        return propertyDescriptions.values();
    }

    /**
     * Attach the description of a property to this bean description.
     * @param propertyDescription description of the property constraints
     */
    void addPropertyDescription(PropertyConstraintDescription propertyDescription) {
        propertyDescriptions.put(propertyDescription.getName(), propertyDescription);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
