package org.jarbframework.constraint.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.jarbframework.utils.Asserts;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Adapts the {@link DatabaseConstraintValidator} to the JSR303 interface.
 * 
 * @author Jeroen van Schagen
 * @since 30-10-2012
 */
public class DatabaseConstrainedAdapter implements ConstraintValidator<DatabaseConstrained, Object>, ApplicationContextAware {

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
        return validator.isValid(bean, entityClass, propertyName, validatorContext);
    }

    @Override
    public void initialize(DatabaseConstrained annotation) {
        entityClass = annotation.entityClass();
        propertyName = annotation.propertyName();
        
        // Load validator from application context
        Asserts.notNull(applicationContext, "Could not create DatabaseConstraintValidator because no application context is provided. Have you registered a LocalValidatorFactoryBean?");
        validator = DatabaseConstraintValidatorRegistry.getInstance(applicationContext, annotation);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
}
