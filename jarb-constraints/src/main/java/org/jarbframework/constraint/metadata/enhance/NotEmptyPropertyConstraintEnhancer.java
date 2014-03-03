package org.jarbframework.constraint.metadata.enhance;

import static org.jarbframework.utils.bean.AnnotationScanner.fieldOrGetter;

import org.hibernate.validator.constraints.NotEmpty;
import org.jarbframework.constraint.metadata.PropertyConstraintDescription;

/**
 * Whenever a property is annotated as {@code NotEmpty}, the minimum length has to be at least 1.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class NotEmptyPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription description) {
        if (fieldOrGetter().hasAnnotation(description.toReference(), NotEmpty.class)) {
            // When a property cannot be empty, it has a minimum length of at least 1
            // If our description already has a greater minimum length, do nothing
            if (description.getMinimumLength() == null || description.getMinimumLength() == 0) {
                description.setMinimumLength(1);
            }
        }
        return description;
    }

}
