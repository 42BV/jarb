package org.jarb.constraint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Describe the constraints of a specific bean.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 *
 * @param <T> type of bean being described
 */
public class BeanConstraintDescription<T> {
    /** Collection of each bean property being described **/
    private final Map<String, PropertyConstraintDescription<?>> propertyDescriptionMap;
    /** Type of bean being described **/
    private final Class<T> beanClass;

    /**
     * Construct a new {@link BeanConstraintDescription}.
     * @param beanClass class of the bean being described
     */
    public BeanConstraintDescription(Class<T> beanClass) {
        this.beanClass = beanClass;
        propertyDescriptionMap = new HashMap<String, PropertyConstraintDescription<?>>();
    }

    /**
     * Retrieve the class of our bean.
     * @return bean class
     */
    public Class<T> getBeanClass() {
        return beanClass;
    }

    /**
     * Include a property description in our global bean description.
     * @param propertyDescription description of the property
     */
    public void addPropertyDescription(PropertyConstraintDescription<?> propertyDescription) {
        propertyDescriptionMap.put(propertyDescription.getPropertyName(), propertyDescription);
    }

    /**
     * Retrieve the description of a specific bean property.
     * @param propertyName name of the property
     * @return property description, or {@code null} if the property
     * describes is not present
     */
    public PropertyConstraintDescription<?> getPropertyDescription(String propertyName) {
        return propertyDescriptionMap.get(propertyName);
    }

    /**
     * Retrieve the type-safe description of a specific bean property.
     * @param <X> property type
     * @param propertyName name of the property
     * @param propertyClass class of the property
     * @return property description, or {@code null} if the property
     * describes is not present
     */
    @SuppressWarnings("unchecked")
    public <X> PropertyConstraintDescription<X> getPropertyDescription(String propertyName, Class<X> propertyClass) {
        return (PropertyConstraintDescription<X>) propertyDescriptionMap.get(propertyName);
    }

    /**
     * Retrieve all available property descriptions of our bean.
     * @return property descriptions
     */
    public Collection<PropertyConstraintDescription<?>> getPropertyDescriptions() {
        return propertyDescriptionMap.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
