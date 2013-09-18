package org.jarbframework.constraint.metadata.enhance;

import static org.jarbframework.utils.bean.AnnotationScanner.fieldOrGetter;

import java.util.Collection;

import javax.validation.constraints.Digits;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;

/**
 * Enhance the property constraint descriptor with @Digits information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DigitsPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyDescription) {
        Collection<Digits> digitsAnnotations = fieldOrGetter().getAnnotations(propertyDescription.toReference(), Digits.class);
        Integer maximumLength = propertyDescription.getMaximumLength();
        Integer fractionLength = propertyDescription.getFractionLength();
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
        propertyDescription.setMaximumLength(maximumLength);
        propertyDescription.setFractionLength(fractionLength);
        return propertyDescription;
    }

}
