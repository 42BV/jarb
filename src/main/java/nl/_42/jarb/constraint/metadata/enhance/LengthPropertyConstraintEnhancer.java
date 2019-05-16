package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.Annotations;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Function;

/**
 * Enhance the property constraint descriptor with @Length information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class LengthPropertyConstraintEnhancer<A extends Annotation> implements PropertyConstraintEnhancer {

    private final Class<A> annotationType;

    private final Function<A, Integer> minimum;
    private final Function<A, Integer> maximum;

    public LengthPropertyConstraintEnhancer(Class<A> annotationType, Function<A, Integer> minimum, Function<A, Integer> maximum) {
        this.annotationType = annotationType;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public void enhance(PropertyConstraintDescription description) {
        Collection<A> annotations = Annotations.getAnnotations(description.toReference(), annotationType);

        Integer minimumLength = description.getMinimumLength();
        Integer maximumLength = description.getMaximumLength();

        for (A annotation : annotations) {
            // Store the highest minimum and lowest maximum length, as this will cause both restrictions to pass
            minimumLength = highest(minimumLength, minimum.apply(annotation));
            maximumLength = lowest(maximumLength, maximum.apply(annotation));
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
