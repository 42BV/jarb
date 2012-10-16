package org.jarbframework.constraint.metadata;

import static org.jarbframework.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import javax.validation.constraints.NotNull;


/**
 * Whenever a property is annotated as @NotNull , it is required.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class NotNullPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription description) {
        if (fieldOrGetter().hasAnnotation(description.toPropertyReference(), NotNull.class)) {
            description.setRequired(true);
        }
        return description;
    }

}
