package org.jarb.constraint;

public interface PropertyConstraintDescriptionEnhancer {

    <T> MutablePropertyConstraintDescription<T> enhance(MutablePropertyConstraintDescription<T> propertyDescription, Class<?> beanClass);

}
