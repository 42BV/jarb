package org.jarb.constraint;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * Default implementation of {@link BeanConstraintMetadataGenerator}.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DefaultBeanConstraintMetadataGenerator implements BeanConstraintMetadataGenerator {
    private List<PropertyConstraintMetadataEnhancer> propertyMetadataEnhancers;

    public DefaultBeanConstraintMetadataGenerator() {
        propertyMetadataEnhancers = new ArrayList<PropertyConstraintMetadataEnhancer>();
    }

    public void setPropertyMetadataEnhancers(List<PropertyConstraintMetadataEnhancer> propertyMetadataEnhancers) {
        Assert.notNull(propertyMetadataEnhancers, "Property metadata enhancers cannot be null");
        this.propertyMetadataEnhancers = propertyMetadataEnhancers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BeanConstraintMetadata<T> describe(Class<T> beanClass) {
        MutableBeanConstraintMetadata<T> entityDescription = new MutableBeanConstraintMetadata<T>(beanClass);
        for (PropertyDescriptor property : BeanUtils.getPropertyDescriptors(beanClass)) {
            entityDescription.addPropertyMetadata(describeProperty(property, beanClass));
        }
        return entityDescription;
    }

    /**
     * Describe the constraints of a specific property.
     * @param property plain property description from java
     * @return property constraint description
     */
    private PropertyConstraintMetadata<?> describeProperty(PropertyDescriptor property, Class<?> beanClass) {
        MutablePropertyConstraintMetadata<?> propertyDescription = createPropertyDescription(property.getName(), property.getPropertyType());
        for (PropertyConstraintMetadataEnhancer propertyDescriptionEnhancer : propertyMetadataEnhancers) {
            propertyDescription = propertyDescriptionEnhancer.enhance(propertyDescription, beanClass);
        }
        return propertyDescription;
    }

    /**
     * Construct a new {@link MutablePropertyConstraintMetadata} for some property.
     * @param <T> type of property being described
     * @param propertyName name of the property being described
     * @param propertyClass class of the property being described
     * @return new plain property description
     */
    private <T> MutablePropertyConstraintMetadata<T> createPropertyDescription(String propertyName, Class<T> propertyClass) {
        return new MutablePropertyConstraintMetadata<T>(propertyName, propertyClass);
    }

}
