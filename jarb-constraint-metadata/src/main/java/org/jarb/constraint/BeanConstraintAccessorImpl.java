package org.jarb.constraint;

import static org.jarbframework.utils.Conditions.notNull;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.jarbframework.utils.bean.PropertyReference;
import org.springframework.beans.BeanUtils;

/**
 * Default implementation of {@link BeanConstraintAccessor}.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class BeanConstraintAccessorImpl implements BeanConstraintAccessor {
    private final List<PropertyConstraintEnhancer> propertyConstraintEnhancers = new ArrayList<PropertyConstraintEnhancer>();

    @Override
    public <T> BeanConstraintDescription<T> describe(Class<T> beanClass) {
        BeanConstraintDescription<T> beanDescription = new BeanConstraintDescription<T>(beanClass);
        for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(beanClass)) {
            beanDescription.addPropertyDescription(describeProperty(beanClass, propertyDescriptor));
        }
        return beanDescription;
    }

    /**
     * Describe the constraints of a specific property.
     * @param beanClass type of bean that contains the property
     * @param propertyDescriptor plain property description from java
     * @return property constraint description
     */
    private PropertyConstraintDescription describeProperty(Class<?> beanClass, PropertyDescriptor propertyDescriptor) {
        PropertyConstraintDescription propertyDescription = createPropertyDescription(beanClass, propertyDescriptor);
        for (PropertyConstraintEnhancer propertyConstraintEnhancer : propertyConstraintEnhancers) {
            propertyDescription = propertyConstraintEnhancer.enhance(propertyDescription);
        }
        return propertyDescription;
    }

    /**
     * Construct a new {@link PropertyConstraintDescription} for some property.
     * @param beanClass type of bean that contains the property
     * @param propertyDescriptor plain property description from java
     * @return new property constraint description
     */
    private PropertyConstraintDescription createPropertyDescription(Class<?> beanClass, PropertyDescriptor propertyDescriptor) {
        PropertyReference propertyReference = new PropertyReference(beanClass, propertyDescriptor.getName());
        return new PropertyConstraintDescription(propertyReference, propertyDescriptor.getPropertyType());
    }

    /**
     * Register a property constraint enhancer to this bean constraint accessor.
     * Whenever a new bean is described, the provided enhancer will be used.
     * @param propertyConstraintEnhancer enhancer used to improve property constraint descriptions
     * @return the same bean constraint accessor, used for chaining
     */
    public BeanConstraintAccessorImpl registerEnhancer(PropertyConstraintEnhancer propertyConstraintEnhancer) {
        propertyConstraintEnhancers.add(notNull(propertyConstraintEnhancer, "Cannot add a null property constraint enhancer"));
        return this;
    }

}
