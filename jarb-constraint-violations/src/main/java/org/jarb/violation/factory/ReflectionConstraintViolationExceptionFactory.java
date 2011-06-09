package org.jarb.violation.factory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jarb.violation.ConstraintViolation;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * Creates constraint violation exceptions using reflection. Whenever no
 * supported constructor could be found, we throw a runtime exception.
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
            throw new IllegalArgumentException("Constructor contains unsupported parameter types");
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
        Object[] arguments = new Object[parameterTypes.length];
        for(int parameterIndex = 0; parameterIndex < parameterTypes.length; parameterIndex++) {
            if(ConstraintViolation.class.equals(parameterTypes[parameterIndex])) {
                arguments[parameterIndex] = violation;
            } else if(Throwable.class.isAssignableFrom(parameterTypes[parameterIndex])) {
                arguments[parameterIndex] = cause;
            } else if(parameterTypes[parameterIndex].isAssignableFrom(ReflectionConstraintViolationExceptionFactory.class)) {
                arguments[parameterIndex] = this;
            }
        }
        // Return the created exception
        return BeanUtils.instantiateClass(exceptionConstructor, arguments);
    }
    
    /**
     * Find the constructor in a class that has the most supported parameter types.
     * Whenever no matching constructor could be found, we throw a runtime exception.
     * @param exceptionClass class declaring our constructors
     * @return first matching constructor, never {@code null}
     */
    @SuppressWarnings("unchecked")
    private static Constructor<? extends Throwable> findBestSupportedConstructor(Class<? extends Throwable> exceptionClass) {
        // Retrieve a list of all constructors, sorted on the amount of parameters it recieves
        List<Constructor<?>> declaredConstructors = new ArrayList<Constructor<?>>();
        declaredConstructors.addAll(Arrays.asList(exceptionClass.getDeclaredConstructors()));
        Collections.sort(declaredConstructors, ConstructorParameterTypeLengthComparator.INSTANCE);
        for(Constructor<?> constructor : declaredConstructors) {
            // Return the first supported constructor, as this will automatically be the "best"
            if(supportsConstructor(constructor)) {
                return (Constructor<? extends Throwable>) constructor;
            }
        }
        // We expect a matching constructor be found
        throw new IllegalStateException("Could not find a supported constructor in '" + exceptionClass.getSimpleName() + "'.");
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
     * Determine if a parameter type is supported.
     * @param parameterType class of the parameter
     * @return {@code true} if the parameter type is supported, else {@code false}
     */
    private static boolean supportsParameterType(Class<?> parameterType) {
        return
            // We support a constraint violation argument
            ConstraintViolation.class.equals(parameterType) ||
            // Throwable (subclass) argument
            Throwable.class.isAssignableFrom(parameterType) ||
            // (Reflection) constraint violation exception factory argument
            parameterType.isAssignableFrom(ReflectionConstraintViolationExceptionFactory.class);
    }
    
    /**
     * Compares two constructors based on the amount of parameters they declare.
     * The constructor with the most number of parameter is placed first.
     */
    private enum ConstructorParameterTypeLengthComparator implements Comparator<Constructor<?>> {
        INSTANCE;
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(Constructor<?> left, Constructor<?> right) {
            return numberOfParameterTypes(right).compareTo(numberOfParameterTypes(left));
        }
        
        /**
         * Count the number of parameter types inside a constructor.
         * @param constructor the constructor that declares the parameter types
         * @return number of parameter types inside the constructor
         */
        private Integer numberOfParameterTypes(Constructor<?> constructor) {
            return Integer.valueOf(constructor.getParameterTypes().length);
        }
        
    }

}
