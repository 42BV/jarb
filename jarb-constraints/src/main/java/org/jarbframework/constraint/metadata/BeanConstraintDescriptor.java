package org.jarbframework.constraint.metadata;

import static org.jarbframework.utils.Asserts.notNull;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.jarbframework.constraint.metadata.enhance.PropertyConstraintEnhancer;
import org.jarbframework.utils.bean.BeanRegistry;
import org.jarbframework.utils.bean.MapBeanRegistry;
import org.jarbframework.utils.bean.PropertyReference;
import org.springframework.beans.BeanUtils;

/**
 * Generates bean constraint metadata.
 * 
 * @author Jeroen van Schagen
 * @since 4-6-2011
 */
public class BeanConstraintDescriptor {

    /**
     * Enhances the property descriptions.
     */
    private final List<PropertyConstraintEnhancer> enhancers = new ArrayList<PropertyConstraintEnhancer>();

    /**
     * Registry of all supported beans.
     */
    private BeanRegistry beanRegistry = new MapBeanRegistry();

    /**
     * Generate all beans constraint meta data.
     * 
     * @return the beans constraint meta data
     */
    public List<BeanConstraintDescription> describeAll() {
        List<BeanConstraintDescription> descriptions = new ArrayList<BeanConstraintDescription>();
        for (Class<?> beanClass : beanRegistry.getAll()) {
            descriptions.add(describeBean(beanClass));
        }
        return descriptions;
    }

    /**
     * Generate bean constraint meta data.
     * 
     * @param beanType class of the bean
     * @return bean constraint meta data
     */
    public BeanConstraintDescription describeBean(String beanType) {
        Class<?> beanClass = beanRegistry.getBeanClass(beanType);
        return describeBean(beanClass);
    }

    /**
     * Generate bean constraint meta data.
     * 
     * @param beanClass class of the bean
     * @return bean constraint meta data
     */
    public BeanConstraintDescription describeBean(Class<?> beanClass) {
        BeanConstraintDescription beanDescription = new BeanConstraintDescription(beanClass);
        for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(beanClass)) {
            if (propertyDescriptor.getPropertyType() != null) {
                beanDescription.addProperty(describeProperty(beanClass, propertyDescriptor));
            }
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
        PropertyConstraintDescription description = doDescribeProperty(beanClass, descriptor);
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
    private PropertyConstraintDescription doDescribeProperty(Class<?> beanClass, PropertyDescriptor descriptor) {
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
    public BeanConstraintDescriptor register(PropertyConstraintEnhancer enhancer) {
        enhancers.add(notNull(enhancer, "Cannot add a null property constraint enhancer"));
        return this;
    }
    
    public void setBeanRegistry(BeanRegistry beanRegistry) {
        this.beanRegistry = beanRegistry;
    }

}
