package org.jarbframework.constraint.metadata;

import static org.jarbframework.utils.Asserts.notNull;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;

import org.jarbframework.constraint.metadata.enhance.PropertyConstraintEnhancer;
import org.jarbframework.utils.bean.BeanRegistry;
import org.jarbframework.utils.bean.MapBeanRegistry;
import org.jarbframework.utils.bean.PropertyReference;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

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
            Field field = ReflectionUtils.findField(beanClass, propertyDescriptor.getName());
            if (field != null) {
                if (field.getType().isAnnotationPresent(Embeddable.class)) {
                    describeEmbeddable(beanDescription, beanClass, field.getType(), propertyDescriptor.getName());
                } else {
                    beanDescription.addProperty(describeProperty(beanClass, propertyDescriptor, propertyDescriptor.getName()));
                }
            }
        }
        return beanDescription;
    }

    /**
     * Describe the constraints of a specific property.
     * 
     * @param beanClass type of bean that contains the property
     * @param descriptor plain property description from java
     * @param path the path to the property from the beanClass.
     * @return property constraint description
     */
    private PropertyConstraintDescription describeProperty(Class<?> beanClass, PropertyDescriptor descriptor, String path) {
        PropertyConstraintDescription description = doDescribeProperty(beanClass, descriptor, path);
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
     * @param path the path to the property from the beanClass.
     * @return new property constraint description
     */
    private PropertyConstraintDescription doDescribeProperty(Class<?> beanClass, PropertyDescriptor descriptor, String path) {
        PropertyReference reference = new PropertyReference(beanClass, path);
        return new PropertyConstraintDescription(reference, descriptor.getPropertyType());
    }

    /**
     * Take an embedable and walk through each property and add it to the beanDescription.
     *
     * @param beanDescription Constains the current descriptions for the bean.
     * @param beanClass The Bean which is described
     * @param embeddable The Embeddable which fields needs to be added to the beanDescription.
     * @param path The current path from the bean to the embeddable field.
     */
    private void describeEmbeddable(BeanConstraintDescription beanDescription, Class<?> beanClass , Class<?> embeddable, String path) {
        for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(embeddable)) {
            Class<?> klass = propertyDescriptor.getPropertyType();

            if (klass != null) {
                if (klass.isAnnotationPresent(Embeddable.class)) {
                    describeEmbeddable(beanDescription, beanClass, klass, path + "." + propertyDescriptor.getName());
                } else {
                    String fullPath = path + "." + propertyDescriptor.getName();
                    beanDescription.addProperty(describeProperty(beanClass, propertyDescriptor, fullPath));
                }
            }
        }
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
