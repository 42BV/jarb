package nl._42.jarb.constraint.metadata;

import nl._42.jarb.constraint.metadata.enhance.PropertyConstraintEnhancer;
import nl._42.jarb.utils.StringUtils;
import nl._42.jarb.utils.bean.BeanRegistry;
import nl._42.jarb.utils.bean.MapBeanRegistry;
import nl._42.jarb.utils.bean.PropertyReference;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import jakarta.persistence.Embeddable;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl._42.jarb.utils.Asserts.notNull;

/**
 * Generates bean constraint metadata.
 * 
 * @author Jeroen van Schagen
 * @since 4-6-2011
 */
public class BeanConstraintDescriptor {

    /**
     * Properties to ignore.
     */
    private final Set<String> ignoredProperties = new HashSet<>();

    /**
     * Enhances the property descriptions.
     */
    private final List<PropertyConstraintEnhancer> enhancers = new ArrayList<>();

    /**
     * Registry of all supported beans.
     */
    private BeanRegistry beanRegistry = new MapBeanRegistry();

    {
        ignoredProperties.add("new");
        ignoredProperties.add("id");
        ignoredProperties.add("class");
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
        BeanConstraintDescription bean = new BeanConstraintDescription(beanClass);

        for (PropertyDescriptor property : BeanUtils.getPropertyDescriptors(beanClass)) {
            Field field = ReflectionUtils.findField(beanClass, property.getName());

            if (field != null) {
                Class<?> propertyType = field.getType();

                if (Collection.class.isAssignableFrom(propertyType)) {
                    Class<?> valueType = (Class<?>) getGenericTypes(field)[0];
                    describeProperty(bean, property, valueType);
                } else if (Map.class.isAssignableFrom(propertyType)) {
                    Class<?> keyType = (Class<?>) getGenericTypes(field)[0];
                    Class<?> valueType = (Class<?>) getGenericTypes(field)[1];
                    describeProperty(bean, property, keyType);
                    describeProperty(bean, property, valueType);
                } else {
                    describeProperty(bean, property, propertyType);
                }
            }
        }

        return bean;
    }

    private Type[] getGenericTypes(Field field) {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        return parameterizedType.getActualTypeArguments();
    }

    private void describeProperty(BeanConstraintDescription bean, PropertyDescriptor descriptor, Class<?> propertyType) {
        PropertyReference property = new PropertyReference(bean.getJavaType(), descriptor.getName());
        describeProperty(bean, property, propertyType);
    }

    private void describeProperty(BeanConstraintDescription bean, PropertyReference property, Class<?> propertyType) {
        if (isIgnored(property)) {
            return; // Skipped
        }

        if (propertyType.isAnnotationPresent(Embeddable.class)) {
            describeEmbeddable(bean, propertyType, property);
        } else {
            PropertyConstraintDescription description = describeProperty(property, propertyType);
            bean.addProperty(description);
        }
    }

    private boolean isIgnored(PropertyReference property) {
        return StringUtils.isBlank(property.getPropertyName()) ||
               ignoredProperties.contains(property.getSimpleName());
    }

    private void describeEmbeddable(BeanConstraintDescription bean, Class<?> embeddableType, PropertyReference property) {
        for (PropertyDescriptor descriptor : BeanUtils.getPropertyDescriptors(embeddableType)) {
            Class<?> propertyType = descriptor.getPropertyType();

            if (propertyType != null) {
                PropertyReference nested = new PropertyReference(property, descriptor.getName());
                describeProperty(bean, nested, propertyType);
            }
        }
    }

    private PropertyConstraintDescription describeProperty(PropertyReference property, Class<?> propertyType) {
        PropertyConstraintDescription description = new PropertyConstraintDescription(property, propertyType);

        for (PropertyConstraintEnhancer enhancer : enhancers) {
            enhancer.enhance(description);
        }

        return description;
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

    public void addIgnoreProperty(String propertyName) {
        this.ignoredProperties.add(propertyName);
    }

}
