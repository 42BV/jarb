package org.jarbframework.constraint.metadata.enhance;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.AnnotationScanner;

/**
 * Whenever a property is annotated as @NotNull , it is required.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class NotNullPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription description) {
        if (AnnotationScanner.hasAnnotation(description.toReference(), javax.validation.constraints.NotNull.class)) {
            description.setRequired(true);
        }
        return description;
    }

}
