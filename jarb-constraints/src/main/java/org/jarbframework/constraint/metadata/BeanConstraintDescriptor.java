package org.jarbframework.constraint.metadata;

import static org.jarbframework.utils.Asserts.notNull;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.jarbframework.constraint.metadata.enhance.PropertyConstraintEnhancer;
import org.jarbframework.utils.bean.PropertyReference;
import org.springframework.beans.BeanUtils;

/**
 * Generates bean constraint metadata.
 * 
 * @author Jeroen van Schagen
 * @since 4-6-2011
 */
public class BeanConstraintDescriptor {

    private final List<PropertyConstraintEnhancer> enhancers = new ArrayList<PropertyConstraintEnhancer>();

    /**
     * Generate bean constraint meta data.
     * 
     * @param beanClass class of the bean
     * @return bean constraint meta data
     */
    public BeanConstraintDescription describe(Class<?> beanClass) {
        BeanConstraintDescription beanDescription = new BeanConstraintDescription(beanClass);
        for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(beanClass)) {
            beanDescription.addProperty(describeProperty(beanClass, propertyDescriptor));
        }
        return beanDescription;
    }

    /**
     * Describe the constraints of a specific property.
     * 
     * @param beanClass type of bean that contains the property
     * @param descriptor plain property description from java
     * @return property constraint description
     */
    private PropertyConstraintDescription describeProperty(Class<?> beanClass, PropertyDescriptor descriptor) {
        PropertyConstraintDescription description = newPropertyDescription(beanClass, descriptor);
        for (PropertyConstraintEnhancer enhancer : enhancers) {
            enhancer.enhance(description);
        }
        return description;
    }

    /**
     * Construct a new {@link PropertyConstraintDescription} for some property.
     * 
     * @param beanClass type of bean that contains the property
     * @param descriptor plain property description from java
     * @return new property constraint description
     */
    private PropertyConstraintDescription newPropertyDescription(Class<?> beanClass, PropertyDescriptor descriptor) {
        PropertyReference reference = new PropertyReference(beanClass, descriptor.getName());
        return new PropertyConstraintDescription(reference, descriptor.getPropertyType());
    }

    /**
     * Register a property constraint enhancer to this bean constraint accessor.
     * Whenever a new bean is described, the provided enhancer will be used.
     * 
     * @param enhancer enhancer used to improve property constraint descriptions
     * @return this bean descriptor, used for chaining
     */
    public BeanConstraintDescriptor registerEnhancer(PropertyConstraintEnhancer enhancer) {
        enhancers.add(notNull(enhancer, "Cannot add a null property constraint enhancer"));

        return this;
    }

}
