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
	
	private static final Class<?>[] SUPPORTED_TYPES = new Class<?>[] { Throwable.class, DatabaseConstraintViolation.class, DatabaseConstraintExceptionFactory.class };
    
	private final DynamicBeanInitializer<? extends Throwable> exceptionInitializer;

    /**
     * Construct a new {@link ReflectionConstraintExceptionFactory}. When using
     * this constructor we will use the first exception constructor that has only supported
     * parameter types. If we cannot find a supported constructor, a runtime exception is thrown.
     * @param exceptionClass class of the exception that should be created.
     */
    public ReflectionConstraintExceptionFactory(Class<? extends Throwable> exceptionClass) {
        this(DynamicBeanInitializer.mostSupported(exceptionClass, SUPPORTED_TYPES));
    }
    
    /**
     * Construct a new {@link ReflectionConstraintExceptionFactory}.
     * @param constructor exception constructor, should consist of only supported parameter types
     */
    public ReflectionConstraintExceptionFactory(Constructor<? extends Throwable> constructor) {
        this(DynamicBeanInitializer.specificSupported(constructor, SUPPORTED_TYPES));
    }

    private ReflectionConstraintExceptionFactory(DynamicBeanInitializer<? extends Throwable> exceptionInitializer) {
		this.exceptionInitializer = exceptionInitializer;
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public Throwable createException(DatabaseConstraintViolation violation, Throwable cause) {
        return exceptionInitializer.initialize(violation, cause, this);
    }

}
