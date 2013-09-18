package org.jarbframework.constraint.metadata.enhance;

import static org.jarbframework.utils.bean.AnnotationScanner.fieldOrGetter;

import java.util.Collection;

import org.hibernate.validator.constraints.Length;
import org.jarbframework.constraint.metadata.PropertyConstraintDescription;

/**
 * Enhance the property constraint descriptor with @Length information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class LengthPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyDescription) {
        Collection<Length> lengthAnnotations = fieldOrGetter().getAnnotations(propertyDescription.toReference(), Length.class);
        Integer minimumLength = propertyDescription.getMinimumLength();
        Integer maximumLength = propertyDescription.getMaximumLength();
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
        propertyDescription.setMinimumLength(minimumLength);
        propertyDescription.setMaximumLength(maximumLength);
        return propertyDescription;
    }

}
