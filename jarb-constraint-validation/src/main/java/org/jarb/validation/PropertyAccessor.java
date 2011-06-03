package org.jarb.validation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

/**
 * Provides access to property values.
 * 
 * @author Jeroen van Schagen
 * @since 01-06-2011
 */
public final class PropertyAccessor {

    /**
     * Retrieve the property value from a bean. Preferably the value is retrieved by an
     * accessor method, but if it could not be found, directly access the field.
     * @param bean the bean container our property
     * @param property description of the property
     * @return the property value, if any
     */
    public static Object getPropertyValue(Object bean, PropertyDescriptor property) {
        Method readMethod = property.getReadMethod();
        if (readMethod == null) { // Retrieve property value directly from field
            Field field = ReflectionUtils.findField(bean.getClass(), property.getName());
            ReflectionUtils.makeAccessible(field);
            return ReflectionUtils.getField(field, bean);
        }
        try {
            return readMethod.invoke(bean);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e); // Property accessor should be accessable
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e); // Exceptions should not occur
        }
    }

    private PropertyAccessor() {
        // Prevent initialization
    }

}
