package org.jarbframework.utils.spring;

import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.utils.Asserts;
import org.jarbframework.utils.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

/**
 * Retrieves beans from the {@link BeanFactory}.
 * 
 * @author Jeroen van Schagen
 * @since Sep 8, 2011
 */
public class SpringBeanFinder {
	
    private final BeanFactory beanFactory;

    /**
     * Construct a new {@link SpringBeanFinder}.
     * @param beanFactory factory that contains the beans
     */
    public SpringBeanFinder(BeanFactory beanFactory) {
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
        
        if (StringUtils.isNotBlank(identifier)) {
            return beanFactory.getBean(identifier, beanClass);
        } else {
            return beanFactory.getBean(beanClass);
        }
    }

    /**
     * Search for a bean based on identifier, or when not specified,
     * based on type. Whenever multiple matching beans are found for
     * the specified type, we will wire based on the default identifier.
     * @param beanClass type of the bean
     * @param identifier identifier of the bean (<b>optional</b>)
     * @param fallbackIdentifier fall-back bean identifier (<b>required</b>)
     * @return matching bean
     */
    public <T> T findUniqueBean(Class<T> beanClass, String fallbackIdentifier) {
        Asserts.hasText(fallbackIdentifier, "Fallback bean identifier cannot be empty.");

        try {
            return beanFactory.getBean(beanClass);
        } catch (NoUniqueBeanDefinitionException be) {
            try {
                return beanFactory.getBean(fallbackIdentifier, beanClass);
            } catch (BeansException dbe) {
                throw be; // Throw the original exception
            }
        }
    }

}
