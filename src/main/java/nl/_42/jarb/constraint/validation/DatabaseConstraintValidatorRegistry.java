package nl._42.jarb.constraint.validation;

import static nl._42.jarb.utils.StringUtils.isNotBlank;

import java.util.Map;

import javax.validation.ValidatorFactory;

import nl._42.jarb.constraint.metadata.database.BeanMetadataRepository;
import nl._42.jarb.utils.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
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
     * @param annotation the DatabaseConstrained annotation used
     * @return the validator to be used
     */
    public static DatabaseConstraintValidator getInstance(ApplicationContext applicationContext, DatabaseConstrained annotation) {
        Asserts.notNull(applicationContext, "Application context is required.");
        if (isNotBlank(annotation.id())) {
            return applicationContext.getBean(annotation.id(), DatabaseConstraintValidator.class);
        } else {
            LOGGER.info("Creating new DatabaseConstraintValidator because no @DatabaseConstrained.id is defined.");
            return buildInstance(applicationContext, annotation);
        }
    }

    private static DatabaseConstraintValidator buildInstance(ApplicationContext applicationContext, DatabaseConstrained annotation) {
        BeanMetadataRepository beanMetadataRepository = getBean(applicationContext, annotation.beanMetadataRepository(), BeanMetadataRepository.class);
        ValidatorFactory validatorFactory = getBean(applicationContext, annotation.factory(), ValidatorFactory.class);
        return new DatabaseConstraintValidator(beanMetadataRepository, validatorFactory.getMessageInterpolator());
    }

    private static <T> T getBean(ApplicationContext applicationContext, String beanName, Class<T> beanClass) {
        if (isNotBlank(beanName)) {
            return applicationContext.getBean(beanName, beanClass);
        } else {
            return getFirstBean(applicationContext, beanClass);
        }
    }

    private static <T> T getFirstBean(ApplicationContext applicationContext, Class<T> beanClass) {
        try {
            return applicationContext.getBean(beanClass);
        } catch (NoUniqueBeanDefinitionException nubde) {
            Map<String, T> beans = applicationContext.getBeansOfType(beanClass, true, true);
            String firstBeanName = beans.keySet().iterator().next();
            LOGGER.warn("Found multiple '{}' beans, using '{}' to create DatabaseConstraintValidator. Please specify an exact bean in @DatabaseConstrained.",  beanClass.getSimpleName(), firstBeanName);
            return beans.get(firstBeanName);
        } catch (NoSuchBeanDefinitionException be) {
            throw new IllegalStateException("Missing required '" + beanClass.getSimpleName() + "' bean to create DatabaseConstraintValidator.", be);
        }
    }

}
