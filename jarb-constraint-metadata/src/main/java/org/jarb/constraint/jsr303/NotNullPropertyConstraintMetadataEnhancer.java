package org.jarb.constraint.jsr303;

import javax.validation.constraints.NotNull;

import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.jarb.constraint.PropertyConstraintMetadataEnhancer;

/**
 * Whenever a property is annotated as @NotNull , it is required.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class NotNullPropertyConstraintMetadataEnhancer implements PropertyConstraintMetadataEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> MutablePropertyConstraintMetadata<T> enhance(MutablePropertyConstraintMetadata<T> propertyMetadata, Class<?> beanClass) {
        if(ConstraintAnnotationScanner.isPropertyAnnotated(beanClass, propertyMetadata.getName(), NotNull.class)) {
            propertyMetadata.setRequired(true);
        }
        return propertyMetadata;
    }

}
