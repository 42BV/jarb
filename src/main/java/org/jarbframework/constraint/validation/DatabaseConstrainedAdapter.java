package org.jarbframework.constraint.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Adapts the {@link DatabaseConstraintValidator} to the JSR303 interface.
 * 
 * @author Jeroen van Schagen
 * @since 30-10-2012
 */
public class DatabaseConstrainedAdapter implements ConstraintValidator<DatabaseConstrained, Object>, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConstrainedAdapter.class);

    /** The base entity class **/
    private Class<?> entityClass;
    
    /** The base property name **/
    private String propertyName;
    
    /** Delegate component that performs the actual constraint checking **/
    private DatabaseConstraintValidator validator;
    
    /** Used to lookup beans in our application context **/
    private ApplicationContext applicationContext;

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext validatorContext) {
        if (validator != null) {
            return validator.isValid(bean, entityClass, propertyName, validatorContext);
        } else {
            LOGGER.info("Could not create DatabaseConstraintValidator because no application context is provided. Have you registered a LocalValidatorFactoryBean?");
            return true;
        }
    }

    @Override
    public void initialize(DatabaseConstrained annotation) {
        entityClass = annotation.entityClass();
        propertyName = annotation.propertyName();
        
        // Load validator from application context
        if (applicationContext != null) {
            validator = DatabaseConstraintValidatorRegistry.getInstance(applicationContext, annotation);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
}
