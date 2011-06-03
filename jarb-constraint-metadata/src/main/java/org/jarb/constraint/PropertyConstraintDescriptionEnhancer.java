package org.jarb.constraint;

public interface PropertyConstraintDescriptionEnhancer {

    <T> PropertyConstraintDescription<T> enhance(PropertyConstraintDescription<T> propertyDescription, Class<?> beanClass);

}
