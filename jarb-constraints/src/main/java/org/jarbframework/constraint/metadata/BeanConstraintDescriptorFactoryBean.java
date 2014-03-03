package org.jarbframework.constraint.metadata;

import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.utils.Classes;
import org.jarbframework.utils.spring.SingletonFactoryBean;

/**
 * Builds a default bean constraint meta data generator.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 */
public class BeanConstraintDescriptorFactoryBean extends SingletonFactoryBean<BeanConstraintDescriptor> {
    
    private static final String HIBERNATE_VALIDATOR_PACKAGE_NAME = "org.hibernate.validator";

    private final BeanMetadataRepository beanMetadataRepository;

    public BeanConstraintDescriptorFactoryBean(BeanMetadataRepository beanMetadataRepository) {
        this.beanMetadataRepository = beanMetadataRepository;
    }

    @Override
    protected BeanConstraintDescriptor createObject() throws Exception {
        BeanConstraintDescriptor descriptor = new BeanConstraintDescriptor();
        descriptor.registerDefaultEnhancers();
        if (Classes.hasPackage(HIBERNATE_VALIDATOR_PACKAGE_NAME)) {
            descriptor.registerHibernateEnhancers();
        }
        if (beanMetadataRepository != null) {
            descriptor.registerDatabaseEnhancers(beanMetadataRepository);
        }
        return descriptor;
    }

}
