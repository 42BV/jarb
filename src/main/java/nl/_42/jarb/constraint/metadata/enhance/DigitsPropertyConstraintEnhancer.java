package nl._42.jarb.constraint.metadata.enhance;

import java.util.Collection;

import javax.validation.constraints.Digits;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.Annotations;

/**
 * Enhance the property constraint descriptor with @Digits information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class DigitsPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public void enhance(PropertyConstraintDescription description) {
        Collection<javax.validation.constraints.Digits> annotations = 
                Annotations.getAnnotations(description.toReference(), javax.validation.constraints.Digits.class);
        Integer maximumLength = description.getMaximumLength();
        Integer fractionLength = description.getFractionLength();
        for (Digits annotation : annotations) {
            maximumLength = lowest(maximumLength, annotation.integer());
            fractionLength = lowest(fractionLength, annotation.fraction());
        }
        description.setMaximumLength(maximumLength);
        description.setFractionLength(fractionLength);
    }

    private Integer lowest(Integer current, int next) {
        return current != null ? Math.min(current, next) : next;
    }

}
