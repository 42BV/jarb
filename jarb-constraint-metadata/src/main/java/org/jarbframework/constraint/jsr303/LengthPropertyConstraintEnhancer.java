package org.jarbframework.constraint.jsr303;

import static org.jarbframework.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import java.util.Collection;

import org.hibernate.validator.constraints.Length;
import org.jarbframework.constraint.PropertyConstraintDescription;
import org.jarbframework.constraint.PropertyConstraintEnhancer;

/**
 * Enhance the property constraint descriptor with @Length information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class LengthPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription description) {
        Collection<Length> lengthAnnotations = fieldOrGetter().collectAnnotations(description.toPropertyReference(), Length.class);
        Integer minimumLength = description.getMinimumLength();
        Integer maximumLength = description.getMaximumLength();
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
        description.setMinimumLength(minimumLength);
        description.setMaximumLength(maximumLength);
        return description;
    }

}
