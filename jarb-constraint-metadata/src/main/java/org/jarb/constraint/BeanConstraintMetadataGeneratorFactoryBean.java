package org.jarb.constraint;

import java.util.ArrayList;
import java.util.List;

import org.jarb.constraint.database.DatabasePropertyConstraintDescriptionEnhancer;
import org.jarb.constraint.database.column.EntityAwareColumnMetadataRepository;
import org.jarb.utils.spring.SingletonFactoryBean;
import org.springframework.util.Assert;

/**
 * Builds a default bean constraint metadata generator.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 */
public class BeanConstraintMetadataGeneratorFactoryBean extends SingletonFactoryBean<BeanConstraintMetadataGenerator> {
    private EntityAwareColumnMetadataRepository columnMetadataRepository;

    public void setColumnMetadataRepository(EntityAwareColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BeanConstraintMetadataGenerator createObject() throws Exception {
        Assert.state(columnMetadataRepository != null, "Column metadata repository is required!");
        DefaultBeanConstraintMetadataGenerator descriptor = new DefaultBeanConstraintMetadataGenerator();
        List<PropertyConstraintMetadataEnhancer> propertyDescriptionEnhancers = new ArrayList<PropertyConstraintMetadataEnhancer>();
        propertyDescriptionEnhancers.add(new DatabasePropertyConstraintDescriptionEnhancer(columnMetadataRepository));
        propertyDescriptionEnhancers.add(new LengthPropertyConstraintMetadataEnhancer());
        propertyDescriptionEnhancers.add(new DigitsPropertyConstraintMetadataEnhancer());
        propertyDescriptionEnhancers.add(new NotNullPropertyConstraintMetadataEnhancer());
        descriptor.setPropertyMetadataEnhancers(propertyDescriptionEnhancers);
        return descriptor;
    }

}
