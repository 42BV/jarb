package org.jarb.constraint.jsr303;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.jarb.constraint.PropertyConstraintMetadataEnhancer;

/**
 * Enhance the property constraint descriptor with @Length information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class LengthPropertyConstraintMetadataEnhancer implements PropertyConstraintMetadataEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> MutablePropertyConstraintMetadata<T> enhance(MutablePropertyConstraintMetadata<T> propertyMetadata, Class<?> beanClass) {
        List<Length> lengthAnnotations = ConstraintAnnotationScanner.getPropertyAnnotations(beanClass, propertyMetadata.getPropertyName(), Length.class);
        Integer minimumLength = propertyMetadata.getMinimumLength();
        Integer maximumLength = propertyMetadata.getMaximumLength();
        for (Length lengthAnnotation : lengthAnnotations) {
            if (minimumLength != null) {
                // Store the highest minimum length, as this will cause both lenght restrictions to pass
                minimumLength = Math.max(minimumLength, lengthAnnotation.min());
            } else {
                minimumLength = lengthAnnotation.min();
            }
            if (maximumLength != null) {
                // Store the lowest maximum length, as this will cause both lenght restrictions to pass
                maximumLength = Math.min(maximumLength, lengthAnnotation.max());
            } else {
                maximumLength = lengthAnnotation.max();
            }
        }
        propertyMetadata.setMinimumLength(minimumLength);
        propertyMetadata.setMaximumLength(maximumLength);
        return propertyMetadata;
    }

}
