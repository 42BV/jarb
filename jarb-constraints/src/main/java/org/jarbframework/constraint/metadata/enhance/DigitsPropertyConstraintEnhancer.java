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
    public PropertyConstraintDescription enhance(PropertyConstraintDescription description) {
        Collection<javax.validation.constraints.Digits> annotations = 
                fieldOrGetter().getAnnotations(description.toReference(), javax.validation.constraints.Digits.class);
        Integer maximumLength = description.getMaximumLength();
        Integer fractionLength = description.getFractionLength();
        for (Digits annotation : annotations) {
            maximumLength = lowest(maximumLength, annotation.integer());
            fractionLength = lowest(maximumLength, annotation.fraction());
        }
        description.setMaximumLength(maximumLength);
        description.setFractionLength(fractionLength);
        return description;
    }

    private Integer lowest(Integer current, Integer next) {
        return current != null ? Math.min(current, next) : next;
    }

}
