package org.jarb.utils;

import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import java.lang.annotation.Annotation;
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
     * Determine if a class has a specific annotation.
     * 
     * @param clazz the class being checked for an annotation
     * @param annotationClass type of annotation being checked on
     * @return {@code true} if the annotation was found, else {@code false}
     */
    public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return clazz.getAnnotation(annotationClass) != null;
    }
    
    // Construction

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
            throw new IllegalArgumentException(
                String.format("%s has not declared a nullary constructor.", clazz.getName()),
                e
            );
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
    
    // Methods
    
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
            throw new IllegalArgumentException(
                String.format("%s has not declared a '%s' method", target.getClass().getName(), methodName)
            );
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
    
    // Fields
    
    /**
     * Determine if an object has some field.
     * 
     * @param bean the target that should contain our field
     * @param fieldName name of the field
     * @return {@code true} if the bean has this field, {@code false}
     */
    public static boolean hasField(Object bean, String fieldName) {
        return findField(bean.getClass(), fieldName) != null;
    }
    
    /**
     * Determine if an object has some field.
     * 
     * @param bean the target that should contain our field
     * @param field the field that should be contained
     * @return {@code true} if the bean has this field, {@code false}
     */
    public static boolean hasField(Object bean, Field field) {
        return field.getDeclaringClass().isAssignableFrom(bean.getClass());
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
            throw new IllegalArgumentException(
                String.format("%s has not declared a '%s' field", target.getClass().getName(), fieldName)
            );
        }
        return field.getType();
    }

    /**
     * Retrieve the field value of a certain {@link Object}. When the field name does not
     * exist inside the object, or its sub-classes, a runtime exception will be thrown.
     * 
     * @param target object that contains the field
     * @param fieldName name of the field that should be read
     * @return current value of the field
     */
    public static Object getFieldValue(Object target, String fieldName) {
        Field field = findField(target.getClass(), fieldName);
        if (field == null) {
            throw new IllegalArgumentException(
                String.format("%s has not declared a '%s' field", target.getClass().getName(), fieldName)
            );
        }
        return getFieldValue(target, field);
    }
    
    /**
     * Retrieve the field value of a certain {@link Object}.
     * 
     * @param target object that contains the field
     * @param field the field being accessed
     * @return current field value
     */
    public static Object getFieldValue(Object target, Field field) {
        makeAccessible(field);
        return getField(field, target);
    }

    /**
     * Modify the field value of a certain {@link Object}. When the field name does not
     * exist inside the object, or its sub-classes, a runtime exception will be thrown.
     * 
     * @param target object that contains the field
     * @param fieldName name of the field that should be modified
     * @param newValue new value of the field
     */
    public static void setFieldValue(Object target, String fieldName, Object newValue) {
        Field field = findField(target.getClass(), fieldName);
        if (field == null) {
            throw new IllegalArgumentException(
                String.format("%s has not declared a '%s' field", target.getClass().getName(), fieldName)
            );
        }
        setFieldValue(target, field, newValue);
    }
    
    /**
     * Modify the field value of a certain {@link Object}. 
     * 
     * @param target object that contains the field
     * @param field the field being modified
     * @param newValue new value of the field
     */
    public static void setFieldValue(Object target, Field field, Object newValue) {
        makeAccessible(field);
        setField(field, target, newValue);
    }
    
    // Suppresses default constructor, ensuring non-instantiability.
    private ReflectionUtils() {
        super();
    }

}
