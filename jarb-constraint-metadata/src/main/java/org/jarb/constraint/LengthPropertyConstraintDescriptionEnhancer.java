package org.jarb.constraint;

import java.util.List;

import org.hibernate.validator.constraints.Length;

/**
 * Enhance the property constraint descriptor with @Length information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class LengthPropertyConstraintDescriptionEnhancer implements PropertyConstraintDescriptionEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> MutablePropertyConstraintDescription<T> enhance(MutablePropertyConstraintDescription<T> propertyDescription, Class<?> beanClass) {
        List<Length> lengthAnnotations = ConstraintAnnotationUtil.getPropertyAnnotations(beanClass, propertyDescription.getPropertyName(), Length.class);
        Integer minimumLength = propertyDescription.getMinimumLength();
        Integer maximumLength = propertyDescription.getMaximumLength();
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
        propertyDescription.setMinimumLength(minimumLength);
        propertyDescription.setMaximumLength(maximumLength);
        return propertyDescription;
    }

}
