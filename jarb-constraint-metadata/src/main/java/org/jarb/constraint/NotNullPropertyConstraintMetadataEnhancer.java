package org.jarb.constraint;

import javax.validation.constraints.NotNull;

/**
 * Enhance the property constraint descriptor with @NotNull information.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class NotNullPropertyConstraintMetadataEnhancer implements PropertyConstraintMetadataEnhancer {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> MutablePropertyConstraintMetadata<T> enhance(MutablePropertyConstraintMetadata<T> propertyDescription, Class<?> beanClass) {
        if(ConstraintAnnotationUtil.hasPropertyAnnotations(beanClass, propertyDescription.getPropertyName(), NotNull.class)) {
            propertyDescription.setRequired(true);
        }
        return propertyDescription;
    }

}
