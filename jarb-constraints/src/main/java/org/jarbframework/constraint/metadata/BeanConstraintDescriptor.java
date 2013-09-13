package org.jarbframework.constraint.metadata;

/**
 * Generates bean constraint metadata.
 * 
 * @author Jeroen van Schagen
 * @since 4-6-2011
 */
public interface BeanConstraintDescriptor {

    /**
     * Generate bean constraint meta data.
     * @param beanClass class of the bean
     * @return bean constraint meta data
     */
    BeanConstraintDescription describe(Class<?> beanClass);

}
