package org.jarb.constraint;

public interface BeanConstraintDescriptor {

    <T> BeanConstraintDescription<T> describe(Class<T> beanClass);

}
