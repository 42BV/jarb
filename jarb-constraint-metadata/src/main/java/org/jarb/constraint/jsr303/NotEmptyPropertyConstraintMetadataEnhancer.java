package org.jarb.constraint.jsr303;

import org.hibernate.validator.constraints.NotEmpty;
import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.jarb.constraint.PropertyConstraintMetadataEnhancer;

/**
 * Whenever a property is annotated as @NotEmpty , the minimum length has to be atleast 1.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class NotEmptyPropertyConstraintMetadataEnhancer implements PropertyConstraintMetadataEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> MutablePropertyConstraintMetadata<T> enhance(MutablePropertyConstraintMetadata<T> propertyMetadata, Class<?> beanClass) {
        if(ConstraintAnnotationScanner.isPropertyAnnotated(beanClass, propertyMetadata.getName(), NotEmpty.class)) {
            // When a property cannot be empty, it has a minimum length of atleast 1
            // If our description already has a greater minimum length, do nothing
            if(propertyMetadata.getMinimumLength() == null || propertyMetadata.getMinimumLength() == 0) {
                propertyMetadata.setMinimumLength(1);
            }
        }
        return propertyMetadata;
    }

}
