package org.jarbframework.constraint.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.jarbframework.utils.bean.PropertyReference;
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

    /** Reference to the bean property, when not the root. **/
    private PropertyReference root;
	
    /** Delegate component that performs the actual constraint checking **/
    private DatabaseConstraintValidator validator;
    
    /** Used to lookup beans in our application context **/
    private SpringBeanFinder beans;

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext validatorContext) {
        return validator.isValid(bean, root, validatorContext);
    }

    @Override
    public void initialize(DatabaseConstrained annotation) {
    	validator = buildValidator(annotation);
    	if (! Object.class.equals(annotation.entityClass())) {
            this.root = new PropertyReference(annotation.entityClass(), annotation.propertyName());
    	}
    }

	private DatabaseConstraintValidator buildValidator(DatabaseConstrained annotation) {
		try {
            return beans.findBean(DatabaseConstraintValidator.class, annotation.id());
        } catch (NoSuchBeanDefinitionException nsbde) {
        	// Create a new validation bean when none are registered
            return new DatabaseConstraintValidatorFactory(beans).build();
        }
	}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.beans = new SpringBeanFinder(applicationContext);
    }
    
}
