package org.jarb.constraint;

import java.util.ArrayList;
import java.util.List;

import org.jarb.constraint.database.DatabasePropertyConstraintDescriptionEnhancer;
import org.jarb.constraint.database.column.EntityAwareColumnMetadataRepository;
import org.jarb.utils.spring.SingletonFactoryBean;
import org.springframework.util.Assert;

/**
 * Builds a default bean constraint descriptor.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 */
public class BeanConstraintDescriptorFactoryBean extends SingletonFactoryBean<BeanConstraintDescriptor> {
    private EntityAwareColumnMetadataRepository columnMetadataRepository;

    public void setColumnMetadataRepository(EntityAwareColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BeanConstraintDescriptor createObject() throws Exception {
        Assert.state(columnMetadataRepository != null, "Column metadata repository is required!");
        DefaultBeanConstraintDescriptor descriptor = new DefaultBeanConstraintDescriptor();
        List<PropertyConstraintDescriptionEnhancer> propertyDescriptionEnhancers = new ArrayList<PropertyConstraintDescriptionEnhancer>();
        propertyDescriptionEnhancers.add(new DatabasePropertyConstraintDescriptionEnhancer(columnMetadataRepository));
        propertyDescriptionEnhancers.add(new LengthPropertyConstraintDescriptionEnhancer());
        propertyDescriptionEnhancers.add(new DigitsPropertyConstraintDescriptionEnhancer());
        propertyDescriptionEnhancers.add(new NotNullPropertyConstraintDescriptionEnhancer());
        descriptor.setPropertyDescriptionEnhancers(propertyDescriptionEnhancers);
        return descriptor;
    }

}
