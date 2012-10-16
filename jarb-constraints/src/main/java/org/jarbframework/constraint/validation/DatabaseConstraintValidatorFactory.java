package org.jarbframework.constraint.validation;

import javax.validation.MessageInterpolator;
import javax.validation.ValidatorFactory;

import org.jarbframework.constraint.metadata.database.DatabaseConstraintRepository;
import org.jarbframework.utils.orm.SchemaMapper;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.jarbframework.utils.spring.BeanSearcher;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Builds a default {@link DatabaseConstraintValidator}.
 *
 * @author Jeroen van Schagen
 * @since 20-10-2011
 */
public class DatabaseConstraintValidatorFactory {
    public static final String DEFAULT_CONSTRAINT_REPO_ID = "databaseConstraintRepository";
    public static final String DEFAULT_VALIDATOR_FACTORY_ID = "validator";
    public static final String DEFAULT_SCHEMA_MAPPER_ID = "schemaMapper";

    private final BeanSearcher beanSearcher;

    public DatabaseConstraintValidatorFactory(BeanSearcher beanSearcher) {
        this.beanSearcher = beanSearcher;
    }

    public DatabaseConstraintValidator build() {
        DatabaseConstraintValidator validator = new DatabaseConstraintValidator();
        validator.setConstraintRepository(beanSearcher.findBean(DatabaseConstraintRepository.class, null, DEFAULT_CONSTRAINT_REPO_ID));
        validator.setMessageInterpolator(findMessageInterpolator());
        validator.setSchemaMapper(findOrBuildSchemaMapper());
        return validator;
    }

    private MessageInterpolator findMessageInterpolator() {
        ValidatorFactory validatorFactory = beanSearcher.findBean(ValidatorFactory.class, null, DEFAULT_VALIDATOR_FACTORY_ID);
        return validatorFactory.getMessageInterpolator();
    }

    private SchemaMapper findOrBuildSchemaMapper() {
        try {
            return beanSearcher.findBean(SchemaMapper.class, null, DEFAULT_SCHEMA_MAPPER_ID);
        } catch (NoSuchBeanDefinitionException e) {
            return new JpaHibernateSchemaMapper();
        }
    }
}
