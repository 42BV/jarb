package org.jarb.constraint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Mutable implementation of {@link BeanConstraintMetadata}.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 *
 * @param <T> type of bean being described
 */
public class MutableBeanConstraintMetadata<T> implements BeanConstraintMetadata<T> {
    private final Class<T> beanClass;
    private Map<String, PropertyConstraintMetadata<?>> propertyMetadataMap;

    /**
     * Construct a new {@link MutableBeanConstraintMetadata}.
     * @param beanClass class of the bean being described
     */
    public MutableBeanConstraintMetadata(Class<T> beanClass) {
        this.beanClass = beanClass;
        propertyMetadataMap = new HashMap<String, PropertyConstraintMetadata<?>>();
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
    public PropertyConstraintMetadata<?> getPropertyMetadata(String propertyName) {
        return propertyMetadataMap.get(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <X> PropertyConstraintMetadata<X> getPropertyMetadata(String propertyName, Class<X> propertyClass) {
        return (PropertyConstraintMetadata<X>) propertyMetadataMap.get(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<PropertyConstraintMetadata<?>> getPropertiesMetadata() {
        return propertyMetadataMap.values();
    }

    /**
     * Attach the description of a property to this bean description.
     * @param propertyMetadata description of the property constraints
     */
    public void addPropertyMetadata(PropertyConstraintMetadata<?> propertyMetadata) {
        propertyMetadataMap.put(propertyMetadata.getName(), propertyMetadata);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
