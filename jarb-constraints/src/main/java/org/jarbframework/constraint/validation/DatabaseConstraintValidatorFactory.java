package org.jarbframework.constraint.validation;

import javax.validation.ValidatorFactory;

import org.jarbframework.constraint.metadata.database.ColumnMetadataRepository;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.spring.SpringBeanFinder;

/**
 * Constructs {@link DatabaseConstraintValidator} beans.
 *
 * @author Jeroen van Schagen
 * @since 20-10-2011
 */
public class DatabaseConstraintValidatorFactory {
    
    public static final String DEFAULT_SCHEMA_MAPPER_ID = "schemaMapper";
    public static final String DEFAULT_COLUMN_METADATA_REPOSITORY_ID = "databaseConstraintRepository";
    public static final String DEFAULT_VALIDATOR_FACTORY_ID = "validator";

    private final SpringBeanFinder beanFinder;

    public DatabaseConstraintValidatorFactory(SpringBeanFinder beanFinder) {
        this.beanFinder = beanFinder;
    }

    /**
     * Build a new {@link DatabaseConstraintValidator}.
     * @return the database constraint validation bean
     */
    public DatabaseConstraintValidator build() {
        DatabaseConstraintValidator validator = new DatabaseConstraintValidator();
        validator.setSchemaMapper(beanFinder.findBean(SchemaMapper.class, null, DEFAULT_SCHEMA_MAPPER_ID));
        validator.setColumnMetadataRepository(beanFinder.findBean(ColumnMetadataRepository.class, null, DEFAULT_COLUMN_METADATA_REPOSITORY_ID));
        
        ValidatorFactory validatorFactory = beanFinder.findBean(ValidatorFactory.class, null, DEFAULT_VALIDATOR_FACTORY_ID);
		validator.setMessageInterpolator(validatorFactory.getMessageInterpolator());
        return validator;
    }

}
