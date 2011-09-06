package org.jarb.constraint.jsr303;

import static org.jarb.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import java.util.Collection;

import org.hibernate.validator.constraints.Length;
import org.jarb.constraint.PropertyConstraintDescription;
import org.jarb.constraint.PropertyConstraintEnhancer;

/**
 * Enhance the property constraint descriptor with @Length information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class LengthPropertyConstraintMetadataEnhancer implements PropertyConstraintEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyMetadata) {
        Collection<Length> lengthAnnotations = fieldOrGetter().collectAnnotations(propertyMetadata.getPropertyReference(), Length.class);
        Integer minimumLength = propertyMetadata.getMinimumLength();
        Integer maximumLength = propertyMetadata.getMaximumLength();
        for (Length lengthAnnotation : lengthAnnotations) {
            if (minimumLength != null) {
                // Store the highest minimum length, as this will cause both length restrictions to pass
                minimumLength = Math.max(minimumLength, lengthAnnotation.min());
            } else {
                minimumLength = lengthAnnotation.min();
            }
            if (maximumLength != null) {
                // Store the lowest maximum length, as this will cause both length restrictions to pass
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
