package org.jarb.populator.excel.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;

public class BeanPropertyHandler {

    public static Object getValue(Object bean, String propertyName) {
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName);
        if(descriptor != null) {
            Method readMethod = descriptor.getReadMethod();
            if(readMethod != null) {
                return ReflectionUtils.invokeMethod(bean, readMethod);
            }
        }
        if(ReflectionUtils.hasField(bean, propertyName)) {
            return ReflectionUtils.getFieldValue(bean, propertyName);
        } else {
            throw new IllegalArgumentException(
                String.format("Property '%s' does not exist in %s.", propertyName, bean.getClass().getSimpleName())
            );
        }
    }
    
    public static void setValue(Object bean, String propertyName, Object propertyValue) {
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName);
        if(descriptor != null) {
            Method writeMethod = descriptor.getWriteMethod();
            if(writeMethod != null) {
                ReflectionUtils.invokeMethod(bean, writeMethod, propertyValue);
                return; // Value has been modified, stop now
            }
        }
        if(ReflectionUtils.hasField(bean, propertyName)) {
            ReflectionUtils.setFieldValue(bean, propertyName, propertyValue);
        } else {
            throw new IllegalArgumentException(
                String.format("Property '%s' does not exist in %s.", propertyName, bean.getClass().getSimpleName())
            );
        }
    }
    
    public static boolean hasProperty(Object bean, String propertyName) {
        boolean hasDescriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName) != null;
        return hasDescriptor || ReflectionUtils.hasField(bean, propertyName);
    }
    
    private BeanPropertyHandler() {
        // Prevent construction
    }
    
}
