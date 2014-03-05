package org.jarbframework.constraint.violation.factory;

import java.lang.reflect.Constructor;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.utils.bean.BeanBuilder;
import org.jarbframework.utils.bean.ConstructorFinder;

/**
 * Creates constraint violation exceptions using reflection. Whenever no
 * supported constructor could be found, we throw a runtime exception.
 * 
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
public class ReflectionConstraintExceptionFactory implements DatabaseConstraintExceptionFactory {
	
	private static final Class<?>[] SUPPORTED_PARAMETER_TYPES = new Class<?>[] {
	        DatabaseConstraintExceptionFactory.class,
	        DatabaseConstraintViolation.class,
		    Throwable.class
	    };
    
	private final BeanBuilder<?> beanBuilder;

    public ReflectionConstraintExceptionFactory(Class<?> exceptionClass) {
        this(ConstructorFinder.findMostSupportedConstructor(exceptionClass, SUPPORTED_PARAMETER_TYPES));
    }
    
    public <T> ReflectionConstraintExceptionFactory(Constructor<T> constructor) {
        this.beanBuilder = new BeanBuilder<T>(constructor);
    }

    @Override
    public Throwable buildException(DatabaseConstraintViolation violation, Throwable cause) {
        Object bean = beanBuilder.instantiate(violation, cause, this);

        if (bean instanceof DatabaseConstraintExceptionFactory) {
            return ((DatabaseConstraintExceptionFactory) bean).buildException(violation, cause);
        } else if (bean instanceof Throwable) {
            return (Throwable) bean;
        } else {
            throw new UnsupportedOperationException("Unable to create exception from: " + bean);
        }
    }

}
