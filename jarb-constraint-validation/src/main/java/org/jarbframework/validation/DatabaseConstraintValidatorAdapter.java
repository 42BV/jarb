package org.jarbframework.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidatorFactory;

import org.jarbframework.constraint.database.DatabaseConstraintRepository;
import org.jarbframework.utils.spring.BeanSearcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Validates the property values of a bean satisfy our database constraints.
 * Performed database checks include:
 * 
 * <ul>
 *  <li>Not nullable columns cannot have {@code null} values, unless they are generated</li>
 *  <li>Column length can never be exceeded. (char and number based)</li>
 *  <li>Fraction length in number column cannot be exceeded</li>
 * </ul>
 * 
 * Whenever a database constraint is violated, a constraint violation will be
 * added for that property. Multiple constraint violations can occur, even on
 * the same property.
 * 
 * <p>
 * 
 * In order for this validator to work, it has to be constructed by a Spring
 * context aware factory. Also a {@link DatabaseConstraintRepository} bean
 * has to be present in the application context.
 * 
 * @author Jeroen van Schagen
 * @since 23-05-2011
 */
public class DatabaseConstraintValidatorAdapter implements ConstraintValidator<DatabaseConstrained, Object>, ApplicationContextAware {
    public static final String DEFAULT_CONSTRAINT_REPO_ID = "databaseConstraintRepository";
    public static final String DEFAULT_VALIDATOR_FACTORY_ID = "validator";

    private DatabaseConstraintValidator databaseConstraintValidator;
    private BeanSearcher beanAccessor;

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        return databaseConstraintValidator.isValid(bean, context);
    }

    @Override
    public void initialize(DatabaseConstrained annotation) {
        try {
            databaseConstraintValidator = beanAccessor.findBean(DatabaseConstraintValidator.class, annotation.id());
        } catch(NoSuchBeanDefinitionException e) {
            databaseConstraintValidator = buildNewDatabaseConstraintValidator();
        }
    }

    private DatabaseConstraintValidator buildNewDatabaseConstraintValidator() {
        DatabaseConstraintValidator databaseConstraintValidator = new DatabaseConstraintValidator();
        databaseConstraintValidator.setConstraintRepository(beanAccessor.findBean(DatabaseConstraintRepository.class, null, DEFAULT_CONSTRAINT_REPO_ID));
        ValidatorFactory validatorFactory = beanAccessor.findBean(ValidatorFactory.class, null, DEFAULT_VALIDATOR_FACTORY_ID);
        databaseConstraintValidator.setMessageBuilder(new ViolationMessageBuilder(validatorFactory.getMessageInterpolator()));
        return databaseConstraintValidator;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanAccessor = new BeanSearcher(applicationContext);
    }

}
