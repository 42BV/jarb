package org.jarbframework.constraint.validation;

import javax.validation.MessageInterpolator;
import javax.validation.ValidatorFactory;

import org.jarbframework.constraint.metadata.database.ColumnMetadataRepository;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.spring.SpringBeanFinder;

/**
 * Factory that builds a default constraint validator, when none is available.
 *
 * @author Jeroen van Schagen
 * @since 20-10-2011
 */
public class DatabaseConstraintValidatorFactory {
    
    public static final String DEFAULT_SCHEMA_MAPPER_ID = "schemaMapper";
    public static final String DEFAULT_COLUMN_METADATA_REPOSITORY_ID = "databaseConstraintRepository";
    
    public static final String DEFAULT_VALIDATOR_FACTORY_ID = "validator";

    private final SpringBeanFinder beanSearcher;

    public DatabaseConstraintValidatorFactory(SpringBeanFinder beanSearcher) {
        this.beanSearcher = beanSearcher;
    }

    public DatabaseConstraintValidator build() {
        DatabaseConstraintValidator validator = new DatabaseConstraintValidator();
        validator.setSchemaMapper(beanSearcher.findBean(SchemaMapper.class, null, DEFAULT_SCHEMA_MAPPER_ID));
        validator.setColumnMetadataRepository(beanSearcher.findBean(ColumnMetadataRepository.class, null, DEFAULT_COLUMN_METADATA_REPOSITORY_ID));
        validator.setMessageInterpolator(getMessageInterpolatorFromValidatorFactory());
        return validator;
    }

    /**
     * Extracts the message interpolator from the JSR303 validator factory.
     * @return the message interpolator in our validator factory
     */
    private MessageInterpolator getMessageInterpolatorFromValidatorFactory() {
        ValidatorFactory validatorFactory = beanSearcher.findBean(ValidatorFactory.class, null, DEFAULT_VALIDATOR_FACTORY_ID);
        return validatorFactory.getMessageInterpolator();
    }

}
