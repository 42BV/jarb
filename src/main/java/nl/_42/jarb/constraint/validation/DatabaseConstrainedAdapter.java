package nl._42.jarb.constraint.validation;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Adapts the {@link DatabaseConstraintValidator} to the JSR303 interface.
 * 
 * @author Jeroen van Schagen
 * @since 30-10-2012
 */
public class DatabaseConstrainedAdapter implements ConstraintValidator<DatabaseConstrained, Object> {

    /** Base entity class **/
    private Class<?> entityClass;

    /** Base property name **/
    private String propertyName;

    /** Delegate component that performs the actual constraint checking **/
    private DatabaseConstraintValidator validator;

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext validatorContext) {
        return validator.isValid(bean, entityClass, propertyName, validatorContext);
    }

    @Override
    public void initialize(DatabaseConstrained annotation) {
        entityClass = annotation.entityClass();
        propertyName = annotation.propertyName();
    }

    @Autowired
    public void setValidator(DatabaseConstraintValidator validator) {
        this.validator = validator;
    }
    
}
