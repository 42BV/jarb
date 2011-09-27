package org.jarbframework.violation.factory;

import static org.jarbframework.utils.Asserts.notNull;
import static org.jarbframework.utils.Asserts.state;
import static org.springframework.beans.BeanUtils.instantiateClass;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jarbframework.violation.DatabaseConstraintViolation;

/**
 * Creates constraint violation exceptions using reflection. Whenever no
 * supported constructor could be found, we throw a runtime exception.
 * 
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
public class ReflectionConstraintExceptionFactory implements DatabaseConstraintExceptionFactory {
    private final Constructor<? extends Throwable> exceptionConstructor;

    /**
     * Construct a new {@link ReflectionConstraintExceptionFactory}.
     * @param exceptionConstructor exception constructor, should consist of only supported parameter types
     */
    public ReflectionConstraintExceptionFactory(Constructor<? extends Throwable> exceptionConstructor) {
        notNull(exceptionConstructor, "Exception constructor cannot be null");
        state(supportsConstructor(exceptionConstructor), "Constructor contains unsupported parameter types");
        this.exceptionConstructor = exceptionConstructor;
    }

    /**
     * Construct a new {@link ReflectionConstraintExceptionFactory}. When using
     * this constructor we will use the first exception constructor that has only supported
     * parameter types. If we cannot find a supported constructor, a runtime exception is thrown.
     * @param exceptionClass class of the exception that should be created.
     */
    public ReflectionConstraintExceptionFactory(Class<? extends Throwable> exceptionClass) {
        this(findBestSupportedConstructor(exceptionClass));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Throwable createException(DatabaseConstraintViolation violation, Throwable cause) {
        return instantiateClass(exceptionConstructor, buildArguments(violation, cause));
    }
    
    // Build an array of construction arguments
    private Object[] buildArguments(DatabaseConstraintViolation violation, Throwable cause) {
        final Class<?>[] parameterTypes = exceptionConstructor.getParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];
        for (int parameterIndex = 0; parameterIndex < parameterTypes.length; parameterIndex++) {
            if (DatabaseConstraintViolation.class.equals(parameterTypes[parameterIndex])) {
                arguments[parameterIndex] = violation;
            } else if (Throwable.class.isAssignableFrom(parameterTypes[parameterIndex])) {
                arguments[parameterIndex] = cause;
            } else if (parameterTypes[parameterIndex].isAssignableFrom(ReflectionConstraintExceptionFactory.class)) {
                arguments[parameterIndex] = this;
            }
        }
        return arguments;
    }

    /**
     * Find the constructor in a class that has the most supported parameter types.
     * Whenever no matching constructor could be found, we throw a runtime exception.
     * @param exceptionClass class declaring our constructors
     * @return first matching constructor, never {@code null}
     */
    @SuppressWarnings("unchecked")
    private static Constructor<? extends Throwable> findBestSupportedConstructor(Class<? extends Throwable> exceptionClass) {
        Constructor<? extends Throwable> supportedConstructor = null;
        
        // Return the first supported constructor, as this will automatically be the "best"
        List<Constructor<?>> declaredConstructors = new ArrayList<Constructor<?>>();
        declaredConstructors.addAll(Arrays.asList(exceptionClass.getDeclaredConstructors()));
        Collections.sort(declaredConstructors, ConstructorParameterTypeLengthComparator.INSTANCE);
        for (Constructor<?> constructor : declaredConstructors) {
            if (supportsConstructor(constructor)) {
                supportedConstructor = (Constructor<? extends Throwable>) constructor;
                break;
            }
        }
        
        return notNull(supportedConstructor, "Could not find a supported constructor in '" + exceptionClass.getSimpleName() + "'.");
    }

    private static boolean supportsConstructor(Constructor<?> constructor) {
        boolean supported = true;
        for (Class<?> parameterType : constructor.getParameterTypes()) {
            if (!supportsParameterType(parameterType)) {
                supported = false;
                break;
            }
        }
        return supported;
    }

    private static boolean supportsParameterType(Class<?> parameterType) {
        return DatabaseConstraintViolation.class.equals(parameterType)
                || Throwable.class.isAssignableFrom(parameterType)
                || parameterType.isAssignableFrom(ReflectionConstraintExceptionFactory.class);
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
