package org.jarbframework.constraint.metadata;

/**
 * Generates bean constraint metadata.
 * 
 * @author Jeroen van Schagen
 * @since 4-6-2011
 */
public interface BeanConstraintDescriptor {

    /**
     * Generate bean constraint metadata.
     * @param <T> type of bean
     * @param beanClass class of the bean
     * @return bean constraint metadata
     */
    <T> BeanConstraintDescription<T> describe(Class<T> beanClass);

}
