package org.jarb.constraint;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.jarb.utils.bean.PropertyReference;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * Default implementation of {@link BeanConstraintAccessor}.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class BeanConstraintAccessorImpl implements BeanConstraintAccessor {
    private List<PropertyConstraintMetadataEnhancer> propertyMetadataEnhancers;

    public BeanConstraintAccessorImpl() {
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
        BeanConstraintMetadata<T> entityDescription = new BeanConstraintMetadata<T>(beanClass);
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
        PropertyReference propertyReference = new PropertyReference(beanClass, property.getName());
        PropertyConstraintMetadata<?> propertyDescription = createPropertyDescription(propertyReference, property.getPropertyType());
        for (PropertyConstraintMetadataEnhancer propertyDescriptionEnhancer : propertyMetadataEnhancers) {
            propertyDescription = propertyDescriptionEnhancer.enhance(propertyDescription);
        }
        return propertyDescription;
    }

    /**
     * Construct a new {@link PropertyConstraintMetadata} for some property.
     * @param <T> type of property being described
     * @param propertyName name of the property being described
     * @param propertyClass class of the property being described
     * @return new plain property description
     */
    private <T> PropertyConstraintMetadata<T> createPropertyDescription(PropertyReference propertyReference, Class<T> propertyClass) {
        return new PropertyConstraintMetadata<T>(propertyReference, propertyClass);
    }

}
