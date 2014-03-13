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
    
    public static final String DEFAULT_METADATA_REPOSITORY_ID = "beanMetadataRepository";

    public static final String DEFAULT_VALIDATOR_FACTORY_ID = "validator";

    private final BeanLocator beanFinder;

    public DatabaseConstraintValidatorFactory(BeanLocator beanFinder) {
        this.beanFinder = beanFinder;
    }

    /**
     * Build a new {@link DatabaseConstraintValidator}.
     * 
     * @return the database constraint validation bean
     */
    public DatabaseConstraintValidator build() {
        BeanMetadataRepository beanMetadataRepository = beanFinder.findUniqueBean(BeanMetadataRepository.class, DEFAULT_METADATA_REPOSITORY_ID);
        ValidatorFactory validatorFactory = beanFinder.findUniqueBean(ValidatorFactory.class, DEFAULT_VALIDATOR_FACTORY_ID);
        return new DatabaseConstraintValidator(beanMetadataRepository, validatorFactory.getMessageInterpolator());
    }

}
