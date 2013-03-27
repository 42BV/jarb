package org.jarbframework.constraint.violation.factory.bean;

import static org.springframework.beans.BeanUtils.instantiateClass;

import java.lang.reflect.Constructor;

import com.google.common.base.Preconditions;

/**
 * Initializes beans dynamically based on the provided arguments. This initializer
 * automatically places the provided arguments in the required constructor order.
 * 
 * @author Jeroen van Schagen
 * @since 05-02-2013
 *
 * @param <T> type of the bean
 */
public class BeanBuilder<T> {
        
    private final Constructor<T> constructor;

    public BeanBuilder(Constructor<T> constructor) {
        Preconditions.checkNotNull(constructor, "Constructor cannot be null");
        this.constructor = constructor;
    }

    public T instantiate(Object... arguments) {
        Object[] invocationArguments = getInstantiationArguments(arguments);
        return instantiateClass(constructor, invocationArguments);
    }

    private Object[] getInstantiationArguments(Object[] arguments) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] invocationArguments = new Object[parameterTypes.length];
        for (int parameterIndex = 0; parameterIndex < parameterTypes.length; parameterIndex++) {
            Class<?> parameterType = parameterTypes[parameterIndex];
            invocationArguments[parameterIndex] = findFirstOfType(parameterType, arguments);
        }
        return invocationArguments;
    }

    private Object findFirstOfType(Class<?> requiredType, Object[] values) {
        Object result = null;
        for(Object value : values) {
            if(value != null && requiredType.isAssignableFrom(value.getClass())) {
                result = value;
                break;
            }
        }
        return result;
    }
    
}
