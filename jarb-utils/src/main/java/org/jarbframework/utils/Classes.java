package org.jarbframework.utils;


/**
 * Utility class for {@link Class} instances.
 *
 * @author Jeroen van Schagen
 * @since Jun 27, 2014
 */
public class Classes {

    /**
     * Retrieve a class by name, whenever not found a runtime exception
     * will be thrown.
     * @param className the class name
     * @param <T> class type of instance
     * @return the class with that name
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> forName(String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Determine if a class has a specific method.
     * 
     * @param clazz the class
     * @param methodName the method name
     * @param parameterTypes the parameter types
     * @return {@code true} if the method is found
     */
    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            clazz.getMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException nsme) {
            return false;
        }
    }

    /**
     * Determine if a class with that name is on the classpath.
     * 
     * @param className the class name
     * @return {@code true} if the class is found
     */
    public static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Determine if a package with that name is on the classpath.
     * 
     * @param packageName the package name
     * @return {@code true} if the package is found
     */
    public static boolean hasPackage(String packageName) {
        return Package.getPackage(packageName) != null;
    }

}
