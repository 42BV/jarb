package org.jarbframework.constraint.violation.factory;

import static org.springframework.beans.BeanUtils.instantiateClass;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jarbframework.utils.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes beans dynamically based on the provided arguments. This initializer
 * automatically places the provided arguments in the desired order.
 * 
 * @author Jeroen van Schagen
 * @since 05-02-2013
 *
 * @param <T> type of the bean
 */
class DynamicBeanInitializer<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicBeanInitializer.class);
	
	private final Constructor<T> constructor;

	private DynamicBeanInitializer(Constructor<T> constructor) {
        Asserts.notNull(constructor, "Constructor cannot be null");
		this.constructor = constructor;
	}
	
	public static <T> DynamicBeanInitializer<T> specificSupported(Constructor<T> constructor, Class<?>... supportedTypes) {
		Asserts.state(isSupportedConstructor(constructor, supportedTypes), "Constructor contains unsupported parameter types.");
		return new DynamicBeanInitializer<T>(constructor);
	}

	@SuppressWarnings("unchecked")
	public static <T> DynamicBeanInitializer<T> mostSupported(Class<T> beanClass, Class<?>... supportedTypes) {
        List<Constructor<?>> declaredConstructors = new ArrayList<Constructor<?>>();
        declaredConstructors.addAll(Arrays.asList(beanClass.getDeclaredConstructors()));
        Collections.sort(declaredConstructors, ConstructorParameterTypeLengthComparator.INSTANCE);

		Constructor<T> supportedConstructor = null;
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            if (isSupportedConstructor(declaredConstructor, supportedTypes)) {
                supportedConstructor = (Constructor<T>) declaredConstructor;
                break;
            }
        }
        
        if(supportedConstructor == null) {
        	throw new IllegalStateException("Could not find a supported constructor in '" + beanClass.getSimpleName() + "'.");
        }
		
		LOGGER.debug("Constructing bean with {}.", supportedConstructor);
		return new DynamicBeanInitializer<T>(supportedConstructor);
	}
	
	private static boolean isSupportedConstructor(Constructor<?> constructor, Class<?>... supportedTypes) {
		boolean supported = true;
		for(Class<?> parameterType : constructor.getParameterTypes()) {
			if(!isSupportedParameterType(parameterType, supportedTypes)) {
				supported = false;
				break;
			}
		}
		return supported;
	}
	
	private static boolean isSupportedParameterType(Class<?> parameterType, Class<?>[] supportedTypes) {
		boolean supported = false;
		for(Class<?> supportedType : supportedTypes) {
			if(supportedType.isAssignableFrom(parameterType)) {
				supported = true;
				break;
			}
		}
		return supported;
	}

	public T initialize(Object... arguments) {
		Object[] invocationArguments = buildInvocationArguments(arguments);
        return instantiateClass(constructor, invocationArguments);
	}

    private Object[] buildInvocationArguments(Object[] arguments) {
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
