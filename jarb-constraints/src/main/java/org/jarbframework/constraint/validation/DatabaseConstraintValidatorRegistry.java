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
    public static DatabaseConstraintValidator getValidator(ApplicationContext applicationContext, DatabaseConstrained annotation) {
        if (isNotBlank(annotation.id())) {
            return applicationContext.getBean(annotation.id(), DatabaseConstraintValidator.class);
        } else {
            LOGGER.info("Creating new DatabaseConstraintValidator because no @DatabaseConstrained.id is defined.");
            return buildValidator(applicationContext, annotation);
        }
    }

    /**
     * Build a new {@link DatabaseConstraintValidator}.
     * 
     * @param applicationContext the application context
     * @return the newly created validator
     */
    static DatabaseConstraintValidator buildValidator(ApplicationContext applicationContext, DatabaseConstrained annotation) {
        BeanMetadataRepository beanMetadataRepository = getBean(applicationContext, annotation.beanMetadataRepository(), BeanMetadataRepository.class);
        ValidatorFactory validatorFactory = getBean(applicationContext, annotation.factory(), ValidatorFactory.class);
        return new DatabaseConstraintValidator(beanMetadataRepository, validatorFactory.getMessageInterpolator());
    }

    private static <T> T getBean(ApplicationContext applicationContext, String beanName, Class<T> beanClass) {
        if (isNotBlank(beanName)) {
            return applicationContext.getBean(beanName, beanClass);
        } else {
            return applicationContext.getBean(beanClass);
        }
    }

}
