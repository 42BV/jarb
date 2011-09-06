package org.jarb.constraint;

import java.util.ArrayList;
import java.util.List;

import org.jarb.constraint.database.DatabaseConstraintRepository;
import org.jarb.constraint.database.DatabasePropertyConstraintDescriptionEnhancer;
import org.jarb.constraint.jsr303.AnnotationPropertyConstraintMetadataTypeEnhancer;
import org.jarb.constraint.jsr303.DigitsPropertyConstraintMetadataEnhancer;
import org.jarb.constraint.jsr303.LengthPropertyConstraintMetadataEnhancer;
import org.jarb.constraint.jsr303.NotEmptyPropertyConstraintMetadataEnhancer;
import org.jarb.constraint.jsr303.NotNullPropertyConstraintMetadataEnhancer;
import org.jarb.utils.spring.SingletonFactoryBean;
import org.springframework.util.Assert;

/**
 * Builds a default bean constraint metadata generator.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 */
public class BeanConstraintAccessorFactoryBean extends SingletonFactoryBean<BeanConstraintAccessor> {
    private DatabaseConstraintRepository databaseConstraintRepository;

    public void setDatabaseConstraintRepository(DatabaseConstraintRepository databaseConstraintRepository) {
        this.databaseConstraintRepository = databaseConstraintRepository;
    }

    @Override
    protected BeanConstraintAccessor createObject() throws Exception {
        Assert.state(databaseConstraintRepository != null, "Database constraint repository is required");
        BeanConstraintAccessorImpl descriptor = new BeanConstraintAccessorImpl();
        List<PropertyConstraintEnhancer> propertyConstraintEnhancers = new ArrayList<PropertyConstraintEnhancer>();
        propertyConstraintEnhancers.add(new DatabasePropertyConstraintDescriptionEnhancer(databaseConstraintRepository));
        propertyConstraintEnhancers.add(new LengthPropertyConstraintMetadataEnhancer());
        propertyConstraintEnhancers.add(new DigitsPropertyConstraintMetadataEnhancer());
        propertyConstraintEnhancers.add(new NotNullPropertyConstraintMetadataEnhancer());
        propertyConstraintEnhancers.add(new NotEmptyPropertyConstraintMetadataEnhancer());
        propertyConstraintEnhancers.add(new AnnotationPropertyConstraintMetadataTypeEnhancer());
        descriptor.setPropertyConstraintEnhancers(propertyConstraintEnhancers);
        return descriptor;
    }

}
