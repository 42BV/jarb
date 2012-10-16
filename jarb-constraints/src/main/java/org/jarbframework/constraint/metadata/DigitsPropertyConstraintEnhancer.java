package org.jarbframework.constraint.metadata;

import static org.jarbframework.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import java.util.Collection;

import javax.validation.constraints.Digits;


/**
 * Enhance the property constraint descriptor with @Digits information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DigitsPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription description) {
        Collection<Digits> digitsAnnotations = fieldOrGetter().collectAnnotations(description.toPropertyReference(), Digits.class);
        Integer maximumLength = description.getMaximumLength();
        Integer fractionLength = description.getFractionLength();
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
        description.setMaximumLength(maximumLength);
        description.setFractionLength(fractionLength);
        return description;
    }

}
