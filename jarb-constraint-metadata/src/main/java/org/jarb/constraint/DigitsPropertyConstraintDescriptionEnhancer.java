package org.jarb.constraint;

import java.util.List;

import javax.validation.constraints.Digits;

/**
 * Enhance the property constraint descriptor with @Digits information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DigitsPropertyConstraintDescriptionEnhancer implements PropertyConstraintDescriptionEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> MutablePropertyConstraintDescription<T> enhance(MutablePropertyConstraintDescription<T> propertyDescription, Class<?> beanClass) {
        List<Digits> digitsAnnotations = ConstraintAnnotationUtil.getPropertyAnnotations(beanClass, propertyDescription.getPropertyName(), Digits.class);
        Integer maximumLength = propertyDescription.getMaximumLength();
        Integer fractionLength = propertyDescription.getFractionLength();
        for(Digits digitsAnnotation : digitsAnnotations) {
            int precision = digitsAnnotation.integer() + digitsAnnotation.fraction();
            if (maximumLength != null) {
                // Store the lowest maximum length, as this will cause both lenght restrictions to pass
                maximumLength = Math.min(maximumLength, precision);
            } else {
                maximumLength = precision;
            }
            if (fractionLength != null) {
                // Store the lowest fraction length, as this will cause both lenght restrictions to pass
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
