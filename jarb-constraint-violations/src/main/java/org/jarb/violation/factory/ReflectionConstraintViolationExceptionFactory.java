package org.jarb.violation.factory;

import static org.springframework.beans.BeanUtils.instantiateClass;

import org.jarb.violation.ConstraintViolation;

/**
 * Creates constraint violation exceptions based on an exception class. Note
 * that this factory will only work for exceptions that have a constraint
 * violation constructor.
 * 
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
public class ReflectionConstraintViolationExceptionFactory implements ConstraintViolationExceptionFactory {
    private final Class<? extends Throwable> exceptionClass;

    /**
     * Construct a new {@link ReflectionConstraintViolationExceptionFactory}.
     * @param exceptionClass class of the exception that should be created
     */
    public ReflectionConstraintViolationExceptionFactory(Class<? extends Throwable> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Throwable createException(ConstraintViolation violation, Throwable cause) {
        Throwable exception = null;
        try {
            exception = instantiateClass(exceptionClass.getConstructor(ConstraintViolation.class), violation);
        } catch (NoSuchMethodException noViolationConstructor) {
            try {
                exception = instantiateClass(exceptionClass.getConstructor());
            } catch (NoSuchMethodException noNullaryConstructor) {
                throw new IllegalStateException("Could not find a nullary or constraint violation constructor for " + exceptionClass.getName());
            }
        }
        return exception;
    }

}
