package org.jarb.violation.factory;

import java.lang.reflect.Constructor;

import org.jarb.violation.ConstraintViolation;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * Creates constraint violation exceptions using reflection. Note that we only
 * support constructors with supported parameter types (violation, throwable).
 * Whenever no matching constructor could be found, we throw a runtime exception.
 * 
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
public class ReflectionConstraintViolationExceptionFactory implements ConstraintViolationExceptionFactory {
    private final Constructor<? extends Throwable> exceptionConstructor;
    
    /**
     * Construct a new {@link ReflectionConstraintViolationExceptionFactory}.
     * @param exceptionConstructor exception constructor, should consist of only supported parameter types
     */
    public ReflectionConstraintViolationExceptionFactory(Constructor<? extends Throwable> exceptionConstructor) {
        Assert.notNull(exceptionConstructor, "Exception constructor cannot be null");
        if(!supportsConstructor(exceptionConstructor)) {
            throw new IllegalArgumentException("Constructor contains unsupported parameter types, can only be a violation or throwable.");
        }
        this.exceptionConstructor = exceptionConstructor;
    }

    /**
     * Construct a new {@link ReflectionConstraintViolationExceptionFactory}. When using
     * this constructor we will use the first exception constructor that has only supported
     * parameter types. If we cannot find a supported constructor, a runtime excepion is thrown.
     * @param exceptionClass class of the exception that should be created.
     */
    public ReflectionConstraintViolationExceptionFactory(Class<? extends Throwable> exceptionClass) {
        this(findBestSupportedConstructor(exceptionClass));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Throwable createException(ConstraintViolation violation, Throwable cause) {
        // Build an array of construction arguments
        final Class<?>[] parameterTypes = exceptionConstructor.getParameterTypes();
        Object[] parameterValues = new Object[parameterTypes.length];
        for(int parameterIndex = 0; parameterIndex < parameterTypes.length; parameterIndex++) {
            if(ConstraintViolation.class.isAssignableFrom(parameterTypes[parameterIndex])) {
                parameterValues[parameterIndex] = violation;
            } else if(Throwable.class.isAssignableFrom(parameterTypes[parameterIndex])) {
                parameterValues[parameterIndex] = cause;
            }
        }
        // Return the created exception
        return BeanUtils.instantiateClass(exceptionConstructor, parameterValues);
    }
    
    /**
     * Find the constructor in a class that has the most supported parameter types.
     * Whenever no matching constructor could be found, we throw a runtime exception.
     * @param exceptionClass class declaring our constructors
     * @return first matching constructor, never {@code null}
     */
    @SuppressWarnings("unchecked")
    private static Constructor<? extends Throwable> findBestSupportedConstructor(Class<? extends Throwable> exceptionClass) {
        Constructor<? extends Throwable> result = null;
        for(Constructor<?> constructor : exceptionClass.getDeclaredConstructors()) {
            if(supportsConstructor(constructor)) {
                // Store only the "best" constructor, meaning it has the most supported parameter types
                // TODO: Create a comparator to place the best constructors first, and then return the first supported
                if(result == null || constructor.getParameterTypes().length > result.getParameterTypes().length) {
                    result = (Constructor<? extends Throwable>) constructor;
                }
            }
        }
        if(result == null) {
            // We expect a matching constructor be found
            throw new IllegalStateException("Could not find a supported constructor in '" + exceptionClass.getSimpleName() + "'.");
        }
        return result;
    }
    
    /**
     * Determine if we support a constructor. Constructors are only supported when
     * all parameter types are supported.
     * @param constructor the constructor being checked
     * @return {@code true} if the constructor is supported, else {@code false}
     */
    private static boolean supportsConstructor(Constructor<?> constructor) {
        boolean supported = true;
        for(Class<?> parameterType : constructor.getParameterTypes()) {
            // Whenever one parameter type is not supported, the constructor cannot be used
            if(!supportsParameterType(parameterType)) {
                supported = false;
                break;
            }
        }
        return supported;
    }
    
    /**
     * Determine if a parameter type is supported. We only support constraint
     * violation and throwable (cause) parameters.
     * @param parameterType class of the parameter
     * @return {@code true} if the parameter type is supported, else {@code false}
     */
    private static boolean supportsParameterType(Class<?> parameterType) {
        return ConstraintViolation.class.isAssignableFrom(parameterType) || Throwable.class.isAssignableFrom(parameterType);
    }

}
