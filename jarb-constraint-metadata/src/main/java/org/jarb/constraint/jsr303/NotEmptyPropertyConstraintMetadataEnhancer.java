package org.jarb.constraint.jsr303;

import static org.jarb.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import org.hibernate.validator.constraints.NotEmpty;
import org.jarb.constraint.PropertyConstraintDescription;
import org.jarb.constraint.PropertyConstraintEnhancer;

/**
 * Whenever a property is annotated as @NotEmpty , the minimum length has to be at least 1.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class NotEmptyPropertyConstraintMetadataEnhancer implements PropertyConstraintEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyMetadata) {
        if (fieldOrGetter().hasAnnotation(propertyMetadata.getPropertyReference(), NotEmpty.class)) {
            // When a property cannot be empty, it has a minimum length of at least 1
            // If our description already has a greater minimum length, do nothing
            if (propertyMetadata.getMinimumLength() == null || propertyMetadata.getMinimumLength() == 0) {
                propertyMetadata.setMinimumLength(1);
            }
        }
        return propertyMetadata;
    }

}
