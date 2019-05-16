package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.Annotations;

import java.lang.annotation.Annotation;

/**
 * Whenever a property is annotated as @NotEmpty, the minimum length has to be at least 1.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class NotEmptyPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    private final Class<? extends Annotation> annotationType;

    public NotEmptyPropertyConstraintEnhancer(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    @Override
    public void enhance(PropertyConstraintDescription description) {
        if (Annotations.hasAnnotation(description.toReference(), annotationType)) {
            // When a property cannot be empty, it has a minimum length of at least 1
            // If our description already has a greater minimum length, do nothing
            if (description.getMinimumLength() == null || description.getMinimumLength() == 0) {
                description.setMinimumLength(1);
            }
        }
    }

}
