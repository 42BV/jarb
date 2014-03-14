package org.jarbframework.constraint.validation;

import javax.validation.ValidatorFactory;

import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.utils.spring.BeanLocator;

/**
 * Constructs {@link DatabaseConstraintValidator} beans.
 *
 * @author Jeroen van Schagen
 * @since 20-10-2011
 */
public class DatabaseConstraintValidatorFactory {
    
    public static final String DEFAULT_VALIDATOR_FACTORY_ID = "validator";

    /**
     * Build a new {@link DatabaseConstraintValidator}.
     * 
     * @param beans provides the beans from our context
     * @return the database constraint validation bean
     */
    public static DatabaseConstraintValidator build(BeanLocator beans) {
        BeanMetadataRepository beanMetadataRepository = beans.getSingleBean(BeanMetadataRepository.class);
        ValidatorFactory validatorFactory = beans.getSingleBean(ValidatorFactory.class, DEFAULT_VALIDATOR_FACTORY_ID);
        return new DatabaseConstraintValidator(beanMetadataRepository, validatorFactory.getMessageInterpolator());
    }

}
