package org.jarbframework.constraint.metadata;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.jarbframework.utils.Asserts.notNull;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.constraint.metadata.enhance.AnnotationPropertyTypeEnhancer;
import org.jarbframework.constraint.metadata.enhance.ClassPropertyTypeEnhancer;
import org.jarbframework.constraint.metadata.enhance.DatabaseGeneratedPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.DatabaseSchemaPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.DigitsPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.LengthPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.NotEmptyPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.NotNullPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.PropertyConstraintEnhancer;
import org.jarbframework.utils.Classes;
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
            beanDescription.addPropertyDescription(describeProperty(beanClass, propertyDescriptor));
        }
        return beanDescription;
    }

    /**
     * Describe the constraints of a specific property.
     * 
     * @param beanClass type of bean that contains the property
     * @param propertyDescriptor plain property description from java
     * @return property constraint description
     */
    private PropertyConstraintDescription describeProperty(Class<?> beanClass, PropertyDescriptor propertyDescriptor) {
        PropertyConstraintDescription propertyDescription = newPropertyDescription(beanClass, propertyDescriptor);
        for (PropertyConstraintEnhancer enhancer : enhancers) {
            propertyDescription = enhancer.enhance(propertyDescription);
        }
        return propertyDescription;
    }

    /**
     * Construct a new {@link PropertyConstraintDescription} for some property.
     * 
     * @param beanClass type of bean that contains the property
     * @param propertyDescriptor plain property description from java
     * @return new property constraint description
     */
    private PropertyConstraintDescription newPropertyDescription(Class<?> beanClass, PropertyDescriptor propertyDescriptor) {
        PropertyReference propertyReference = new PropertyReference(beanClass, propertyDescriptor.getName());
        return new PropertyConstraintDescription(propertyReference, propertyDescriptor.getPropertyType());
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
    
    /**
     * Register all default constraint enhancers.
     * 
     * @return this bean descriptor, used for chaining
     */
    public BeanConstraintDescriptor registerDefaultEnhancers() {
        registerEnhancer(new LengthPropertyConstraintEnhancer());
        registerEnhancer(new DigitsPropertyConstraintEnhancer());
        registerEnhancer(new NotNullPropertyConstraintEnhancer());
        registerEnhancer(new NotEmptyPropertyConstraintEnhancer());
        
        registerEnhancer(new ClassPropertyTypeEnhancer(String.class, "text"));
        registerEnhancer(new ClassPropertyTypeEnhancer(Date.class, "date"));
        registerEnhancer(new ClassPropertyTypeEnhancer(Number.class, "number"));
        registerEnhancer(new AnnotationPropertyTypeEnhancer(Email.class, "email"));
        registerEnhancer(new AnnotationPropertyTypeEnhancer(CreditCardNumber.class, "credid_card"));
        
        return this;
    }

    /**
     * Register the database schema constraint enhancers.
     * 
     * @param beanMetadataRepository the bean metadata repository
     * @return this bean descriptor, used for chaining
     */
    public BeanConstraintDescriptor registerDatabaseEnhancers(BeanMetadataRepository beanMetadataRepository) {
        registerEnhancer(new DatabaseSchemaPropertyConstraintEnhancer(beanMetadataRepository));
        registerEnhancer(new DatabaseGeneratedPropertyConstraintEnhancer());
        
        return this;
    }

    /**
     * Register all custom constraint enhancers.
     * 
     * @param basePackage the base package to scan for enhancers
     * @return this bean descriptor, used for chaining
     */
    public BeanConstraintDescriptor registerCustomEnhancers(String basePackage) {
        if (isNotBlank(basePackage)) {
            Set<Class<?>> enhancerClasses = Classes.getAllOfType(basePackage, PropertyConstraintEnhancer.class);
            for (Class<?> enhancerClass : enhancerClasses) {
                registerEnhancer((PropertyConstraintEnhancer) BeanUtils.instantiateClass(enhancerClass));
            }
        }

        return this;
    }

}
