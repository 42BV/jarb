package org.jarb.violation.factory;

import java.lang.reflect.Constructor;

import org.jarb.violation.ConstraintViolation;
import org.springframework.beans.BeanUtils;

/**
 * Creates constraint violation exceptions based on an exception class. Note
 * that this factory will only work for exceptions that have a constraint
 * violation constructor.
 * 
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
public class ReflectionConstraintViolationExceptionFactory implements ConstraintViolationExceptionFactory {
    private Class<? extends Throwable> exceptionClass;

    public void setExceptionClass(Class<? extends Throwable> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    /**
     * Create a new {@link ReflectionConstraintViolationExceptionFactory} for a specific exception class.
     * @param exceptionClass class of the exception to generate
     * @return new factory instance that generates the exception
     */
    public static ReflectionConstraintViolationExceptionFactory forClass(Class<? extends Throwable> exceptionClass) {
        ReflectionConstraintViolationExceptionFactory factory = new ReflectionConstraintViolationExceptionFactory();
        factory.setExceptionClass(exceptionClass);
        return factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Throwable createException(ConstraintViolation violation) {
        Constructor<? extends Throwable> constructor;
        try {
            constructor = exceptionClass.getConstructor(ConstraintViolation.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find a constraint violation constructor for " + exceptionClass.getName(), e);
        }
        return BeanUtils.instantiateClass(constructor, violation);
    }

}
