package org.jarb.constraint;

/**
 * Enhances property constraint meta-data with additional information.
 * 
 * @author Jeroen van Schagen
 * @since 4-6-2011
 */
public interface PropertyConstraintMetadataEnhancer {

    /**
     * Enhance a property constraint meta-data object.
     * @param <T> type of property being described
     * @param propertyMetadata modifiable property meta-data object
     * @return (modified) property meta-data object
     */
    <T> PropertyConstraintMetadata<T> enhance(PropertyConstraintMetadata<T> propertyMetadata);

}
