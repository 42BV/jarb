package org.jarb.constraint;

import java.util.ArrayList;
import java.util.List;

import org.jarb.constraint.database.DatabasePropertyConstraintDescriptionEnhancer;
import org.jarb.constraint.database.column.EntityAwareColumnMetadataRepository;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class BeanConstraintDescriptorFactoryBean extends AbstractFactoryBean<BeanConstraintDescriptor> {
    private EntityAwareColumnMetadataRepository columnMetadataRepository;

    public void setColumnMetadataRepository(EntityAwareColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getObjectType() {
        return BeanConstraintDescriptor.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BeanConstraintDescriptor createInstance() throws Exception {
        DefaultBeanConstraintDescriptor descriptor = new DefaultBeanConstraintDescriptor();
        List<PropertyConstraintDescriptionEnhancer> propertyDescriptionEnhancers = new ArrayList<PropertyConstraintDescriptionEnhancer>();
        propertyDescriptionEnhancers.add(new DatabasePropertyConstraintDescriptionEnhancer(columnMetadataRepository));
        propertyDescriptionEnhancers.add(new LengthPropertyConstraintDescriptionEnhancer());
        descriptor.setPropertyDescriptionEnhancers(propertyDescriptionEnhancers);
        return descriptor;
    }

}
