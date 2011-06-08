package org.jarb.constraint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Mutable implementation of {@link BeanConstraintDescription}.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 *
 * @param <T> type of bean being described
 */
public class MutableBeanConstraintDescription<T> implements BeanConstraintDescription<T> {
    private final Class<T> beanClass;
    private Map<String, PropertyConstraintDescription<?>> propertyDescriptions;

    /**
     * Construct a new {@link MutableBeanConstraintDescription}.
     * @param beanClass class of the bean being described
     */
    public MutableBeanConstraintDescription(Class<T> beanClass) {
        this.beanClass = beanClass;
        propertyDescriptions = new HashMap<String, PropertyConstraintDescription<?>>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getBeanType() {
        return beanClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyConstraintDescription<?> getPropertyDescription(String propertyName) {
        return propertyDescriptions.get(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <X> PropertyConstraintDescription<X> getPropertyDescription(String propertyName, Class<X> propertyClass) {
        return (PropertyConstraintDescription<X>) propertyDescriptions.get(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<PropertyConstraintDescription<?>> getPropertyDescriptions() {
        return propertyDescriptions.values();
    }

    /**
     * Attach the description of a property to this bean description.
     * @param propertyDescription description of the property
     */
    public void addPropertyDescription(PropertyConstraintDescription<?> propertyDescription) {
        propertyDescriptions.put(propertyDescription.getPropertyName(), propertyDescription);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
