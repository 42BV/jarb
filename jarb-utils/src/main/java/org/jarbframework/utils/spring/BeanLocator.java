package org.jarbframework.utils.spring;

import static org.jarbframework.utils.Asserts.hasText;
import static org.jarbframework.utils.Asserts.notNull;
import static org.jarbframework.utils.StringUtils.isNotBlank;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.util.StringUtils;

/**
 * Retrieves beans from the {@link BeanFactory}.
 * 
 * @author Jeroen van Schagen
 * @since Sep 8, 2011
 */
public class BeanLocator {
	
    private final BeanFactory beanFactory;

    /**
     * Construct a new {@link BeanLocator}.
     * 
     * @param beanFactory factory that contains the beans
     */
    public BeanLocator(BeanFactory beanFactory) {
        this.beanFactory = notNull(beanFactory, "Bean factory cannot be null.");
    }

    /**
     * Search for a bean based on identifier, or when not specified,
     * based on type. Note that when matching based on type, we only
     * expect one matching bean to be found.
     * 
     * @param beanClass type of the bean
     * @param identifier identifier of the bean (<b>optional</b>)
     * @return matching bean
     */
    public <T> T findBean(Class<T> beanClass, String identifier) {
        notNull(beanClass, "Bean class cannot be null.");

        if (isNotBlank(identifier)) {
            return beanFactory.getBean(identifier, beanClass);
        } else {
            return beanFactory.getBean(beanClass);
        }
    }
    
    /**
     * Search for a single bean by type. Whenever multiple matching beans
     * are found, we use the class name as identifier.
     * 
     * @param beanClass type of the bean
     * @return the matching bean
     */
    public <T> T getSingleBean(Class<T> beanClass) {
        notNull(beanClass, "Bean class cannot be null.");
        
        String defaultIdentifier = StringUtils.uncapitalize(beanClass.getSimpleName());
        return getSingleBean(beanClass, defaultIdentifier);
    }

    /**
     * Search for a single bean by type. Whenever multiple matching beans
     * are found, we use the default identifier.
     * 
     * @param beanClass type of the bean
     * @param defaultIdentifier fall-back bean identifier (<b>required</b>)
     * @return the matching bean
     */
    public <T> T getSingleBean(Class<T> beanClass, String defaultIdentifier) {
        hasText(defaultIdentifier, "Fallback bean identifier cannot be empty.");

        try {
            return beanFactory.getBean(beanClass);
        } catch (NoUniqueBeanDefinitionException be) {
            try {
                return beanFactory.getBean(defaultIdentifier, beanClass);
            } catch (BeansException dbe) {
                throw be; // Throw the original exception
            }
        }
    }

}
