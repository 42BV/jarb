package org.jarbframework.constraint.validation;

import javax.validation.ValidatorFactory;

import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.utils.spring.SpringBeanFinder;

/**
 * Constructs {@link DatabaseConstraintValidator} beans.
 *
 * @author Jeroen van Schagen
 * @since 20-10-2011
 */
public class DatabaseConstraintValidatorFactory {
    
    public static final String DEFAULT_METADATA_REPOSITORY_ID = "beanMetadataRepository";

    public static final String DEFAULT_VALIDATOR_FACTORY_ID = "validator";

    private final SpringBeanFinder beanFinder;

    public DatabaseConstraintValidatorFactory(SpringBeanFinder beanFinder) {
        this.beanFinder = beanFinder;
    }

    /**
     * Build a new {@link DatabaseConstraintValidator}.
     * 
     * @return the database constraint validation bean
     */
    public DatabaseConstraintValidator build() {
        BeanMetadataRepository beanMetadataRepository = beanFinder.findBean(BeanMetadataRepository.class, null, DEFAULT_METADATA_REPOSITORY_ID);
        ValidatorFactory validatorFactory = beanFinder.findBean(ValidatorFactory.class, null, DEFAULT_VALIDATOR_FACTORY_ID);
        return new DatabaseConstraintValidator(beanMetadataRepository, validatorFactory.getMessageInterpolator());
    }

}
