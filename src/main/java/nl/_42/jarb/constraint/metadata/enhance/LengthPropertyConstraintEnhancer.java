package nl._42.jarb.constraint.metadata.enhance;

import java.util.Collection;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.Annotations;

/**
 * Enhance the property constraint descriptor with @Length information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class LengthPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public void enhance(PropertyConstraintDescription description) {
        Collection<org.hibernate.validator.constraints.Length> annotations = 
                Annotations.getAnnotations(description.toReference(), org.hibernate.validator.constraints.Length.class);
        Integer minimumLength = description.getMinimumLength();
        Integer maximumLength = description.getMaximumLength();
        for (org.hibernate.validator.constraints.Length annotation : annotations) {
            // Store the highest minimum and lowest maximum length, as this will cause both restrictions to pass
            minimumLength = highest(minimumLength, annotation.min());
            maximumLength = lowest(maximumLength, annotation.max());
        }
        description.setMinimumLength(minimumLength);
        description.setMaximumLength(maximumLength);
    }
    
    private Integer lowest(Integer current, Integer next) {
        return current != null ? Math.min(current, next) : next;
    }
    
    private Integer highest(Integer current, Integer next) {
        return current != null ? Math.max(current, next) : next;
    }

}
