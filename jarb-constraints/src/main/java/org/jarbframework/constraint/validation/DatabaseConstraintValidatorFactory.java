package org.jarbframework.constraint.validation;

import static org.jarbframework.utils.StringUtils.isNotBlank;

import javax.validation.ValidatorFactory;

import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

/**
 * Constructs {@link DatabaseConstraintValidator} beans.
 *
 * @author Jeroen van Schagen
 * @since 20-10-2011
 */
public class DatabaseConstraintValidatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConstraintValidatorFactory.class);

    /**
     * Retrieve a {@link DatabaseConstraintValidator} 
     * 
     * @param applicationContext the application context
     * @param validatorName the preferred validator name, can be null
     * @return the validator to be used
     */
    public static DatabaseConstraintValidator get(ApplicationContext applicationContext, final String validatorName) {
        if (isNotBlank(validatorName)) {
            return applicationContext.getBean(validatorName, DatabaseConstraintValidator.class);
        } else {
            String[] validatorNames = applicationContext.getBeanNamesForType(DatabaseConstraintValidator.class);
            if (validatorNames.length == 0) {
                return build(applicationContext);
            } else {
                // Whenever multiple validators are defined, just select one
                String firstValidatorName = validatorNames[0];
                if (validatorNames.length > 1) {
                    LOGGER.warn("Multiple DatabaseConstraintValidator beans were found, define a @DatabaseConstrained.id", firstValidatorName);
                }
                return applicationContext.getBean(firstValidatorName, DatabaseConstraintValidator.class);
            }
        }
    }

    /**
     * Build a new {@link DatabaseConstraintValidator}.
     * 
     * @param beanFactory the bean factory
     * @return the newly created validator
     */
    static DatabaseConstraintValidator build(BeanFactory beanFactory) {
        BeanMetadataRepository beanMetadataRepository = findBean(beanFactory, BeanMetadataRepository.class);
        ValidatorFactory validatorFactory = findBean(beanFactory, ValidatorFactory.class);
        return new DatabaseConstraintValidator(beanMetadataRepository, validatorFactory.getMessageInterpolator());
    }

    private static <T> T findBean(BeanFactory beanFactory, Class<T> beanClass) {
        try {
            return beanFactory.getBean(beanClass);
        } catch (NoUniqueBeanDefinitionException nube) {
            // Whenever multiple beans are defined, prefer the bean with the name exactly matching our type
            final String defaultIdentifier = StringUtils.uncapitalize(beanClass.getSimpleName());

            try {
                T bean = beanFactory.getBean(defaultIdentifier, beanClass);
                LOGGER.warn("Multiple {} beans were found, we are now using {}.", beanClass, defaultIdentifier);
                return bean;
            } catch (BeansException dbe) {
                throw new IllegalArgumentException("Could not create DatabaseConstraintValidator because multiple " + beanClass.getSimpleName() + " beans were found.", nube);
            }
        } catch (NoSuchBeanDefinitionException nsbe) {
            throw new IllegalArgumentException("Could not create DatabaseConstraintValidator because no " + beanClass.getSimpleName() + " beans was found.", nsbe);
        }
    }

}
