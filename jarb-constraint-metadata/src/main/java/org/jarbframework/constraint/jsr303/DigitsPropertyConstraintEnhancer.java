package org.jarbframework.constraint.jsr303;

import static org.jarbframework.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import java.util.Collection;

import javax.validation.constraints.Digits;

import org.jarbframework.constraint.PropertyConstraintDescription;
import org.jarbframework.constraint.PropertyConstraintEnhancer;

/**
 * Enhance the property constraint descriptor with @Digits information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DigitsPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyConstraints) {
        Collection<Digits> digitsAnnotations = fieldOrGetter().collectAnnotations(propertyConstraints.toPropertyReference(), Digits.class);
        Integer maximumLength = propertyConstraints.getMaximumLength();
        Integer fractionLength = propertyConstraints.getFractionLength();
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
        propertyConstraints.setMaximumLength(maximumLength);
        propertyConstraints.setFractionLength(fractionLength);
        return propertyConstraints;
    }

}
