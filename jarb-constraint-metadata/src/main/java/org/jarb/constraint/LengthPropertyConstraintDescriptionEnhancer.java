package org.jarb.constraint;

import java.beans.PropertyDescriptor;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;

/**
 * Enhance the property constraint descriptor with all @Length content.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class LengthPropertyConstraintDescriptionEnhancer implements PropertyConstraintDescriptionEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> PropertyConstraintDescription<T> enhance(PropertyConstraintDescription<T> propertyDescription, Class<?> beanClass) {
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(beanClass, propertyDescription.getPropertyName());
        List<Length> lengthAnnotations = ConstraintAnnotationUtil.getPropertyAnnotations(beanClass, propertyDescriptor, Length.class);
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
