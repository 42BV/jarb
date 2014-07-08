package org.jarbframework.constraint.validation;

import static org.jarbframework.utils.StringUtils.isNotBlank;

import javax.validation.ValidatorFactory;

import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Retrieves a {@link DatabaseConstraintValidator}.
 *
 * @author Jeroen van Schagen
 * @since 20-10-2011
 */
public class DatabaseConstraintValidatorRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConstraintValidatorRegistry.class);

    /**
     * Retrieve a {@link DatabaseConstraintValidator}. Whenever no bean is found yet
     * we will create one on demand.
     * 
     * @param applicationContext the application context
     * @param validatorName the preferred validator name, can be null
     * @return the validator to be used
     */
    public static DatabaseConstraintValidator getValidator(ApplicationContext applicationContext, final String validatorName) {
        if (isNotBlank(validatorName)) {
            return applicationContext.getBean(validatorName, DatabaseConstraintValidator.class);
        } else {
            String[] validatorNames = applicationContext.getBeanNamesForType(DatabaseConstraintValidator.class);
            if (validatorNames.length == 0) {
                LOGGER.info("Creating new DatabaseConstraintValidator because no bean was found yet.");
                return buildValidator(applicationContext);
            } else {
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
     * @param applicationContext the application context
     * @return the newly created validator
     */
    static DatabaseConstraintValidator buildValidator(ApplicationContext applicationContext) {
        BeanMetadataRepository beanMetadataRepository = findBean(applicationContext, BeanMetadataRepository.class);
        ValidatorFactory validatorFactory = findBean(applicationContext, ValidatorFactory.class);
        return new DatabaseConstraintValidator(beanMetadataRepository, validatorFactory.getMessageInterpolator());
    }

    private static <T> T findBean(ApplicationContext applicationContext, Class<T> beanClass) {
        String[] beanNames = applicationContext.getBeanNamesForType(beanClass);
        if (beanNames.length == 0) {
            throw new IllegalStateException("Could not create DatabaseConstraintValidator because no " + beanClass.getSimpleName() + " beans was found.");
        } else {
            String firstBeanName = beanNames[0];
            if (beanNames.length > 1) {
                LOGGER.warn("Multiple " + beanClass.getSimpleName() + " beans were found, we are now using " + firstBeanName);
            }
            return applicationContext.getBean(firstBeanName, beanClass);
        }
    }

}
