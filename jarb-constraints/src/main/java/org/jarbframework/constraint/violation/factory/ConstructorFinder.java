package org.jarbframework.constraint.violation.factory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

final class ConstructorFinder {
	
    /**
     * Find the constructor in a bean that has the most supported argument types.
     * 
     * @param <T> the type of constructor to return
     * @param beanClass class that contains our constructors
     * @param supportedTypes all supported parameter types
     * @return the constructor with the most only supported parameter types
     */
	@SuppressWarnings("unchecked")
    public static <T> Constructor<T> findMostSupportedConstructor(final Class<T> beanClass, final Class<?>[] supportedTypes) {
        List<Constructor<?>> constructors = getAllConstructorsSortedOnNumberOfArguments(beanClass);
        for (Constructor<?> constructor : constructors) {
            if (isSupportedConstructor(constructor, supportedTypes)) {
                return (Constructor<T>) constructor;
            }
        }
        throw new IllegalStateException("Could not find a supported constructor.");
	}

    private static <T> List<Constructor<?>> getAllConstructorsSortedOnNumberOfArguments(Class<T> beanClass) {
        List<Constructor<?>> declaredConstructors = new ArrayList<Constructor<?>>();
        declaredConstructors.addAll(Arrays.asList(beanClass.getDeclaredConstructors()));
        Collections.sort(declaredConstructors, new ConstructorParameterTypeLengthComparator());
        return declaredConstructors;
    }
	
	private static boolean isSupportedConstructor(Constructor<?> constructor, Class<?>[] supportedTypes) {
		boolean supported = true;
		for (Class<?> parameterType : constructor.getParameterTypes()) {
            if (!isSupportedParameterType(parameterType, supportedTypes)) {
				supported = false;
				break;
			}
		}
		return supported;
	}
	
	private static boolean isSupportedParameterType(Class<?> parameterType, Class<?>[] supportedTypes) {
		boolean supported = false;
		for (Class<?> supportedType : supportedTypes) {
			if (supportedType.isAssignableFrom(parameterType)) {
				supported = true;
				break;
			}
		}
		return supported;
	}

    private static class ConstructorParameterTypeLengthComparator implements Comparator<Constructor<?>> {

        @Override
        public int compare(Constructor<?> left, Constructor<?> right) {
            return numberOfParameterTypes(right).compareTo(numberOfParameterTypes(left));
        }

        private Integer numberOfParameterTypes(Constructor<?> constructor) {
            return Integer.valueOf(constructor.getParameterTypes().length);
        }
        
    }
	
}
