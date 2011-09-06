package org.jarb.constraint.jsr303;

import static org.jarb.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import javax.validation.constraints.NotNull;

import org.jarb.constraint.PropertyConstraintDescription;
import org.jarb.constraint.PropertyConstraintEnhancer;

/**
 * Whenever a property is annotated as @NotNull , it is required.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class NotNullPropertyConstraintMetadataEnhancer implements PropertyConstraintEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyMetadata) {
        if (fieldOrGetter().hasAnnotation(propertyMetadata.getPropertyReference(), NotNull.class)) {
            propertyMetadata.setRequired(true);
        }
        return propertyMetadata;
    }

}
