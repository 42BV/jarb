package org.jarb.constraint;

import java.util.ArrayList;
import java.util.List;

import org.jarb.constraint.database.DatabasePropertyConstraintDescriptionEnhancer;
import org.jarb.constraint.database.column.EntityAwareColumnMetadataRepository;
import org.jarb.constraint.jsr303.AnnotationPropertyConstraintMetadataTypeEnhancer;
import org.jarb.constraint.jsr303.DigitsPropertyConstraintMetadataEnhancer;
import org.jarb.constraint.jsr303.LengthPropertyConstraintMetadataEnhancer;
import org.jarb.constraint.jsr303.NotEmptyPropertyConstraintMetadataEnhancer;
import org.jarb.constraint.jsr303.NotNullPropertyConstraintMetadataEnhancer;
import org.jarb.utils.SingletonFactoryBean;
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
        Assert.state(columnMetadataRepository != null, "Column metadata repository is required");
        DefaultBeanConstraintMetadataGenerator descriptor = new DefaultBeanConstraintMetadataGenerator();
        List<PropertyConstraintMetadataEnhancer> propertyMetadataEnhancers = new ArrayList<PropertyConstraintMetadataEnhancer>();
        propertyMetadataEnhancers.add(new DatabasePropertyConstraintDescriptionEnhancer(columnMetadataRepository));
        propertyMetadataEnhancers.add(new LengthPropertyConstraintMetadataEnhancer());
        propertyMetadataEnhancers.add(new DigitsPropertyConstraintMetadataEnhancer());
        propertyMetadataEnhancers.add(new NotNullPropertyConstraintMetadataEnhancer());
        propertyMetadataEnhancers.add(new NotEmptyPropertyConstraintMetadataEnhancer());
        propertyMetadataEnhancers.add(new AnnotationPropertyConstraintMetadataTypeEnhancer());
        descriptor.setPropertyMetadataEnhancers(propertyMetadataEnhancers);
        return descriptor;
    }

}
