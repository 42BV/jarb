package org.jarb.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;

/**
 * Provides access to the property value of a bean.
 * 
 * @author Jeroen van Schagen
 * @since 14-05-2011
 */
public class BeanPropertyUtils {

    /**
     * Retrieve the current property value.
     * @param bean the bean containing our property
     * @param propertyName name of the property
     * @return property value
     */
    public static Object getValue(Object bean, String propertyName) {
        // Attempt to retrieve the property value from a public getter method
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName);
        if(descriptor != null) {
            Method readMethod = descriptor.getReadMethod();
            if(readMethod != null) {
                return ReflectionUtils.invokeMethod(bean, readMethod);
            }
        }
        // Otherwise attempt to read the field directly
        if(ReflectionUtils.hasField(bean, propertyName)) {
            return ReflectionUtils.getFieldValue(bean, propertyName);
        } else {
            throw new IllegalArgumentException(
                String.format("Property '%s' does not exist in %s.", propertyName, bean.getClass().getSimpleName())
            );
        }
    }
    
    /**
     * Modify the current property value.
     * @param bean the bean containing our property
     * @param propertyName name of the property
     * @param propertyValue new property value
     */
    public static void setValue(Object bean, String propertyName, Object propertyValue) {
        // Attempt to modify the property value by public setter method
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName);
        if(descriptor != null) {
            Method writeMethod = descriptor.getWriteMethod();
            if(writeMethod != null) {
                ReflectionUtils.invokeMethod(bean, writeMethod, propertyValue);
                return; // Value has been modified, stop now
            }
        }
        // Otherwise attempt to modify the field directly
        if(ReflectionUtils.hasField(bean, propertyName)) {
            ReflectionUtils.setFieldValue(bean, propertyName, propertyValue);
        } else {
            throw new IllegalArgumentException(
                String.format("Property '%s' does not exist in %s.", propertyName, bean.getClass().getSimpleName())
            );
        }
    }
    
    /**
     * Determine if a bean has a certain property.
     * @param bean the bean containing our property
     * @param propertyName name of the property
     * @return {@code true} if the property was found, else {@code false}
     */
    public static boolean hasProperty(Object bean, String propertyName) {
        boolean descriptorFound = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName) != null;
        return descriptorFound || ReflectionUtils.hasField(bean, propertyName);
    }
    
    private BeanPropertyUtils() {
        // Prevent construction
    }
    
}
