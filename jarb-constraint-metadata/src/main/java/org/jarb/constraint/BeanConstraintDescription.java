package org.jarb.constraint;

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
public class BeanConstraintDescription<T> {
    private final Class<T> beanClass;
    private Map<String, PropertyConstraintDescription> properties;

    /**
     * Construct a new {@link BeanConstraintDescription}.
     * @param beanClass class of the bean being described
     */
    public BeanConstraintDescription(Class<T> beanClass) {
        this.beanClass = beanClass;
        properties = new HashMap<String, PropertyConstraintDescription>();
    }

    public Class<T> getBeanType() {
        return beanClass;
    }

    public PropertyConstraintDescription getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public Collection<PropertyConstraintDescription> getProperties() {
        return properties.values();
    }

    /**
     * Attach the description of a property to this bean description.
     * @param propertyMetadata description of the property constraints
     */
    void addPropertyDescription(PropertyConstraintDescription propertyMetadata) {
        properties.put(propertyMetadata.getName(), propertyMetadata);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    
}
