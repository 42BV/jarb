package org.jarbframework.constraint.metadata;

import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.utils.spring.SingletonFactoryBean;

/**
 * Builds a default bean constraint meta data generator.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 */
public class BeanConstraintDescriptorFactoryBean extends SingletonFactoryBean<BeanConstraintDescriptor> {
    
    private final BeanMetadataRepository beanMetadataRepository;

    private final String basePackage;

    public BeanConstraintDescriptorFactoryBean(BeanMetadataRepository beanMetadataRepository, String basePackage) {
        this.beanMetadataRepository = beanMetadataRepository;
        this.basePackage = basePackage;
    }

    @Override
    protected BeanConstraintDescriptor createObject() throws Exception {
        BeanConstraintDescriptor beanDescriptor = new BeanConstraintDescriptor();
        beanDescriptor.registerDefaultEnhancers();
        if (beanMetadataRepository != null) {
            beanDescriptor.registerDatabaseEnhancers(beanMetadataRepository);
        }
        beanDescriptor.registerCustomEnhancers(basePackage);
        return beanDescriptor;
    }

}
