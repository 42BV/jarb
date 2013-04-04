package org.jarbframework.constraint.violation.factory;

import java.lang.reflect.Constructor;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

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
    
	private final BeanBuilder<? extends Throwable> exceptionBuilder;

    public ReflectionConstraintExceptionFactory(Class<? extends Throwable> exceptionClass) {
        this(SupportedConstructorFinder.findMostSupportedConstructor(exceptionClass, SUPPORTED_PARAMETER_TYPES));
    }
    
    public <T extends Throwable> ReflectionConstraintExceptionFactory(Constructor<T> constructor) {
        this.exceptionBuilder = new BeanBuilder<T>(constructor);
    }

	/** {@inheritDoc} */
    @Override
    public Throwable createException(DatabaseConstraintViolation violation, Throwable cause) {
        return exceptionBuilder.instantiate(violation, cause, this);
    }

}
