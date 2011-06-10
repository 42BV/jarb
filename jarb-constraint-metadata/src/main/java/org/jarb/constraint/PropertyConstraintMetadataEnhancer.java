package org.jarb.constraint;

public interface PropertyConstraintMetadataEnhancer {

    <T> MutablePropertyConstraintMetadata<T> enhance(MutablePropertyConstraintMetadata<T> propertyMetadata, Class<?> beanClass);

}
