package org.jarb.constraint;

/**
 * Enhances property constraint metadata with additional information.
 * 
 * @author Jeroen van Schagen
 * @since 4-6-2011
 */
public interface PropertyConstraintMetadataEnhancer {

    /**
     * Enhance a property constraint metadata object.
     * @param <T> type of property being described
     * @param propertyMetadata modifiable property metadata object
     * @param beanClass class of the bean that contains our property
     * @return (modified) property metadata object
     */
    <T> MutablePropertyConstraintMetadata<T> enhance(MutablePropertyConstraintMetadata<T> propertyMetadata, Class<?> beanClass);

}
