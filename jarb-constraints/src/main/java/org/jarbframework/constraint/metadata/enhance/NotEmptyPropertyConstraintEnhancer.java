package org.jarbframework.constraint.metadata.enhance;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.Annotations;

/**
 * Whenever a property is annotated as @NotEmpty, the minimum length has to be at least 1.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class NotEmptyPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public void enhance(PropertyConstraintDescription description) {
        if (Annotations.hasAnnotation(description.toReference(), org.hibernate.validator.constraints.NotEmpty.class)) {
            // When a property cannot be empty, it has a minimum length of at least 1
            // If our description already has a greater minimum length, do nothing
            if (description.getMinimumLength() == null || description.getMinimumLength() == 0) {
                description.setMinimumLength(1);
            }
        }
    }

}
