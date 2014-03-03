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
    public PropertyConstraintDescription enhance(PropertyConstraintDescription description) {
        Collection<Length> annotations = fieldOrGetter().getAnnotations(description.toReference(), Length.class);
        Integer minimumLength = description.getMinimumLength();
        Integer maximumLength = description.getMaximumLength();
        for (Length annotation : annotations) {
            // Store the highest minimum and lowest maximum length, as this will cause both restrictions to pass
            minimumLength = highest(minimumLength, annotation.min());
            maximumLength = lowest(maximumLength, annotation.max());
        }
        description.setMinimumLength(minimumLength);
        description.setMaximumLength(maximumLength);
        return description;
    }
    
    private Integer lowest(Integer current, Integer next) {
        return current != null ? Math.min(current, next) : next;
    }
    
    private Integer highest(Integer current, Integer next) {
        return current != null ? Math.max(current, next) : next;
    }

}
