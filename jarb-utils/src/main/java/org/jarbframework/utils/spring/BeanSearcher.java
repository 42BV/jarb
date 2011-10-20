package org.jarbframework.utils.spring;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.utils.Asserts;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Provides enhanced bean finding functionality on bean factories.
 * @author Jeroen van Schagen
 * @since Sep 8, 2011
 */
public class BeanSearcher {
    private final BeanFactory beanFactory;

    /**
     * Construct a new {@link BeanSearcher}.
     * @param beanFactory factory that contains the beans
     */
    public BeanSearcher(BeanFactory beanFactory) {
        this.beanFactory = notNull(beanFactory, "Bean factory cannot be null.");
    }

    /**
     * Search for a bean based on identifier, or when not specified,
     * based on type. Note that when matching based on type, we only
     * expect one matching bean to be found.
     * @param beanClass type of the bean
     * @param identifier identifier of the bean (<b>optional</b>)
     * @return matching bean
     */
    public <T> T findBean(Class<T> beanClass, String identifier) {
        notNull(beanClass, "Bean class cannot be null.");
        if (isNotBlank(identifier)) {
            // Find the bean based on its specified non-blank identifier
            return beanFactory.getBean(identifier, beanClass);
        } else {
            // Whenever no identifier is specified, look if the bean is unique
            return beanFactory.getBean(beanClass);
        }
    }

    /**
     * Search for a bean based on identifier, or when not specified,
     * based on type. Whenever multiple matching beans are found for
     * the specified type, we will wire based on the default identifier.
     * @param beanClass type of the bean
     * @param identifier identifier of the bean (<b>optional</b>)
     * @param defaultIdentifier default bean identifier (<b>required</b>)
     * @return matching bean
     */
    public <T> T findBean(Class<T> beanClass, String identifier, String defaultIdentifier) {
        try {
            return findBean(beanClass, identifier);
        } catch (NoSuchBeanDefinitionException noBean) {
            // Whenever no regular bean could be found, look for the default bean
            try {
                Asserts.hasText(defaultIdentifier, "Default bean identifier cannot be empty");
                return beanFactory.getBean(defaultIdentifier, beanClass);
            } catch (NoSuchBeanDefinitionException noDefaultBean) {
                throw noBean; // Throw the original exception
            }
        }
    }

}
