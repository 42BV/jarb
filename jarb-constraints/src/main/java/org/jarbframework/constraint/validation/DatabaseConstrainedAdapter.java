package org.jarbframework.constraint.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.jarbframework.utils.spring.SpringBeanFinder;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Adapts the {@link DatabaseConstraintValidator} to the JSR303 interface.
 * 
 * @author Jeroen van Schagen
 * @since 30-10-2012
 */
public class DatabaseConstrainedAdapter implements ConstraintValidator<DatabaseConstrained, Object>, ApplicationContextAware {
    
    /** Delegate component that performs the actual constraint checking **/
    private DatabaseConstraintValidator constraintValidator;
    
    /** Used to lookup beans in our application context **/
    private SpringBeanFinder beanSearcher;

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext validatorContext) {
        return constraintValidator.isValid(bean, validatorContext);
    }

    @Override
    public void initialize(DatabaseConstrained annotation) {
        try {
            constraintValidator = beanSearcher.findBean(DatabaseConstraintValidator.class, annotation.id());
        } catch (NoSuchBeanDefinitionException e) {
            constraintValidator = new DatabaseConstraintValidatorFactory(beanSearcher).build();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.beanSearcher = new SpringBeanFinder(applicationContext);
    }
    
}
