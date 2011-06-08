package org.jarb.constraint;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

/**
 * Default implementation of {@link BeanConstraintDescriptor}.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DefaultBeanConstraintDescriptor implements BeanConstraintDescriptor {
    private List<PropertyConstraintDescriptionEnhancer> propertyDescriptionEnhancers;

    public DefaultBeanConstraintDescriptor() {
        propertyDescriptionEnhancers = new ArrayList<PropertyConstraintDescriptionEnhancer>();
    }

    public void setPropertyDescriptionEnhancers(List<PropertyConstraintDescriptionEnhancer> propertyDescriptionEnhancers) {
        this.propertyDescriptionEnhancers = propertyDescriptionEnhancers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BeanConstraintDescription<T> describe(Class<T> beanClass) {
        MutableBeanConstraintDescription<T> entityDescription = new MutableBeanConstraintDescription<T>(beanClass);
        for (PropertyDescriptor property : BeanUtils.getPropertyDescriptors(beanClass)) {
            entityDescription.addPropertyDescription(describeProperty(property, beanClass));
        }
        return entityDescription;
    }

    /**
     * Describe the constraints of a specific property.
     * @param property plain property description from java
     * @return property constraint description
     */
    private PropertyConstraintDescription<?> describeProperty(PropertyDescriptor property, Class<?> beanClass) {
        MutablePropertyConstraintDescription<?> propertyDescription = createPropertyDescription(property.getName(), property.getPropertyType());
        for (PropertyConstraintDescriptionEnhancer propertyDescriptionEnhancer : propertyDescriptionEnhancers) {
            propertyDescription = propertyDescriptionEnhancer.enhance(propertyDescription, beanClass);
        }
        return propertyDescription;
    }

    /**
     * Construct a new {@link MutablePropertyConstraintDescription} for some property.
     * @param <T> type of property being described
     * @param propertyName name of the property being described
     * @param propertyClass class of the property being described
     * @return new plain property description
     */
    private <T> MutablePropertyConstraintDescription<T> createPropertyDescription(String propertyName, Class<T> propertyClass) {
        return new MutablePropertyConstraintDescription<T>(propertyName, propertyClass);
    }

}
