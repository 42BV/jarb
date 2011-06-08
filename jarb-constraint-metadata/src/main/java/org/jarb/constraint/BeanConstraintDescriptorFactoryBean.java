package org.jarb.constraint;

import java.util.ArrayList;
import java.util.List;

import org.jarb.constraint.database.DatabasePropertyConstraintDescriptionEnhancer;
import org.jarb.constraint.database.column.EntityAwareColumnMetadataRepository;
import org.jarb.utils.spring.SingletonFactoryBean;
import org.springframework.beans.factory.annotation.Required;

public class BeanConstraintDescriptorFactoryBean extends SingletonFactoryBean<BeanConstraintDescriptor> {
    private EntityAwareColumnMetadataRepository columnMetadataRepository;

    @Required
    public void setColumnMetadataRepository(EntityAwareColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BeanConstraintDescriptor createObject() throws Exception {
        DefaultBeanConstraintDescriptor descriptor = new DefaultBeanConstraintDescriptor();
        List<PropertyConstraintDescriptionEnhancer> propertyDescriptionEnhancers = new ArrayList<PropertyConstraintDescriptionEnhancer>();
        propertyDescriptionEnhancers.add(new NotNullPropertyConstraintDescriptionEnhancer());
        propertyDescriptionEnhancers.add(new DatabasePropertyConstraintDescriptionEnhancer(columnMetadataRepository));
        propertyDescriptionEnhancers.add(new LengthPropertyConstraintDescriptionEnhancer());
        propertyDescriptionEnhancers.add(new DigitsPropertyConstraintDescriptionEnhancer());
        descriptor.setPropertyDescriptionEnhancers(propertyDescriptionEnhancers);
        return descriptor;
    }

}
