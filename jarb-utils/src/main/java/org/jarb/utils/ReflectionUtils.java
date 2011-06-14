package org.jarb.utils;

import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class that assists the developer with reflection. There are many other frameworks that provide
 * utilities for reflection, but none of these offer the clean, one method, API that this class does.
 * None of the methods provided in this utility throw checked exceptions, which should make the use
 * of reflection less cumbersome.
 * <p>
 * Internally, this class makes use of the {@link ReflectionUtils}, provided by Spring.
 * 
 * @author Jeroen van Schagen
 */
public final class ReflectionUtils {

    /**
     * Create a new instance of a class based on its nullary (no-arg) constructor. Invoking
     * {@link #instantiate(Class)} will require the corresponding class to have defined a nullary
     * constructor, can be marked private. In case a reflection exception occurs, it will be
     * wrapped and thrown as {@link IllegalStateException}.
     * 
     * @param <T> type of instance to be instantiated
     * @param clazz class of the object that should be instantiated
     * @return new nullary instance of the specified class
     */
    public static <T> T instantiate(Class<T> clazz) {
        try {
            return instantiate(clazz.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            String message = String.format("%s has not declared a nullary constructor.", clazz.getName());
            throw new IllegalArgumentException(message, e);
        }
    }

    /**
     * Create a new instance of a, potentially not accessible {@link Constructor}. In case a
     * reflection exception occurs, it will be wrapped and thrown as {@link IllegalStateException}.
     * 
     * @param <T> type of instance to be instantiated
     * @param constructor constructor being instantiated
     * @param args construction arguments
     * @return constructor instance
     */
    public static <T> T instantiate(Constructor<T> constructor, Object... args) {
        makeAccessible(constructor);
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    /**
     * Invokes the, potentially not accessible, {@link Method} using a specific array of arguments.
     * 
     * @param target object in which the method should be invoked
     * @param methodName name of the method that should be invoked
     * @param args arguments that should be specified during method invocation
     * @return method return value
     */
    public static Object invokeMethod(Object target, String methodName, Object... args) {
        Method method = findMethod(target.getClass(), methodName, getClasses(args));
        if (method == null) {
            String message = String.format("%s has not declared a '%s' method", target.getClass().getName(), methodName);
            throw new IllegalArgumentException(message);
        }
        return invokeMethod(target, method, args);
    }
    
    /**
     * Invokes the, potentially not accessible, {@link Method} using a specific array of arguments.
     * 
     * @param target object on which the method should be invoked
     * @param method method that should be invoked
     * @param args arguments that should be specified during method invocation
     * @return method return value of the method's invocation
     */
    public static Object invokeMethod(Object target, Method method, Object... args) {
        makeAccessible(method); // Delegate method invocation to the spring utility
        return org.springframework.util.ReflectionUtils.invokeMethod(method, target, args);
    }

    
    /**
     * Retrieve the classes from a var-arg array of objects.
     * 
     * @param objects the objects that should have their types retrieved
     * @return every object class, returned in a similar order as the arguments
     */
    public static Class<?>[] getClasses(Object... objects) {
        Class<?>[] types = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            types[i] = objects[i].getClass();
        }
        return types;
    }

    /**
     * Retrieve the field type of a certain {@link Object}. When the field name does not
     * exist inside the object, or its sub-classes, a runtime exception will be thrown.
     * 
     * @param target instance of the object that should have its field type retrieved
     * @param fieldName name of the field that should be read
     * @return type of the field
     */
    public static Class<?> getFieldType(Object target, String fieldName) {
        Field field = findField(target.getClass(), fieldName);
        if (field == null) {
            String message = String.format("%s has not declared a '%s' field", target.getClass().getName(), fieldName);
            throw new IllegalArgumentException(message);
        }
        return field.getType();
    }

    /**
     * Retrieve the field value of a certain {@link Object}. When the field name does not
     * exist inside the object, or its sub-classes, a runtime exception will be thrown.
     * 
     * @param fieldName name of the field that should be read
     * @param target instance of the object that should have its field value retrieved
     * @return current value of the field
     */
    public static Object getFieldValue(Object target, String fieldName) {
        Field field = findField(target.getClass(), fieldName);
        if (field == null) {
            String message = String.format("%s has not declared a '%s' field", target.getClass().getName(), fieldName);
            throw new IllegalArgumentException(message);
        }
        makeAccessible(field);
        return getField(field, target);
    }

    /**
     * Modify the field value of a certain {@link Object}. When the field name does not
     * exist inside the object, or its sub-classes, a runtime exception will be thrown.
     * 
     * @param fieldName name of the field that should be modified
     * @param target instance of the object that should have its field value modified
     * @param newValue new value of the field
     */
    public static void setFieldValue(Object target, String fieldName, Object newValue) {
        Field field = findField(target.getClass(), fieldName);
        if (field == null) {
            String message = String.format("%s has not declared a '%s' field", target.getClass().getName(), fieldName);
            throw new IllegalArgumentException(message);
        }
        makeAccessible(field);
        setField(field, target, newValue);
    }

    // Suppresses default constructor, ensuring non-instantiability.
    private ReflectionUtils() {
        super();
    }

}
