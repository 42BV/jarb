package org.jarbframework.constraint.metadata;

import java.util.Date;

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
import org.jarbframework.utils.spring.SingletonFactoryBean;

/**
 * Builds a default bean constraint meta data generator.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 */
public class BeanConstraintDescriptorFactoryBean extends SingletonFactoryBean<BeanConstraintDescriptor> {
    
    private final BeanMetadataRepository beanMetadataRepository;
    
    public BeanConstraintDescriptorFactoryBean(BeanMetadataRepository beanMetadataRepository) {
        this.beanMetadataRepository = beanMetadataRepository;
    }

    @Override
    protected BeanConstraintDescriptor createObject() throws Exception {
        BeanConstraintDescriptor beanDescriptor = new BeanConstraintDescriptor();
        beanDescriptor.registerEnhancer(new DatabaseSchemaPropertyConstraintEnhancer(beanMetadataRepository));
        beanDescriptor.registerEnhancer(new DatabaseGeneratedPropertyConstraintEnhancer());
        
        beanDescriptor.registerEnhancer(new LengthPropertyConstraintEnhancer());
        beanDescriptor.registerEnhancer(new DigitsPropertyConstraintEnhancer());
        beanDescriptor.registerEnhancer(new NotNullPropertyConstraintEnhancer());
        beanDescriptor.registerEnhancer(new NotEmptyPropertyConstraintEnhancer());
        
        beanDescriptor.registerEnhancer(new ClassPropertyTypeEnhancer(String.class, "text"));
        beanDescriptor.registerEnhancer(new ClassPropertyTypeEnhancer(Date.class, "date"));
        beanDescriptor.registerEnhancer(new ClassPropertyTypeEnhancer(Number.class, "number"));
        beanDescriptor.registerEnhancer(new AnnotationPropertyTypeEnhancer(Email.class, "email"));
        beanDescriptor.registerEnhancer(new AnnotationPropertyTypeEnhancer(CreditCardNumber.class, "credid_card"));
        return beanDescriptor;
    }

}
