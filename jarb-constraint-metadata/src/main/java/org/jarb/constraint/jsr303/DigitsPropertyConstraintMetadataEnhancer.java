package org.jarb.constraint.jsr303;

import java.util.List;

import javax.validation.constraints.Digits;

import org.jarb.constraint.PropertyConstraintDescription;
import org.jarb.constraint.PropertyConstraintEnhancer;

/**
 * Enhance the property constraint descriptor with @Digits information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DigitsPropertyConstraintMetadataEnhancer implements PropertyConstraintEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyMetadata) {
        List<Digits> digitsAnnotations = ConstraintAnnotationScanner.getPropertyAnnotations(propertyMetadata.getPropertyReference(), Digits.class);
        Integer maximumLength = propertyMetadata.getMaximumLength();
        Integer fractionLength = propertyMetadata.getFractionLength();
        for (Digits digitsAnnotation : digitsAnnotations) {
            if (maximumLength != null) {
                // Store the lowest maximum length, as this will cause both length restrictions to pass
                maximumLength = Math.min(maximumLength, digitsAnnotation.integer());
            } else {
                maximumLength = digitsAnnotation.integer();
            }
            if (fractionLength != null) {
                // Store the lowest fraction length, as this will cause both length restrictions to pass
                fractionLength = Math.min(fractionLength, digitsAnnotation.fraction());
            } else {
                fractionLength = digitsAnnotation.fraction();
            }
        }
        propertyMetadata.setMaximumLength(maximumLength);
        propertyMetadata.setFractionLength(fractionLength);
        return propertyMetadata;
    }

}
