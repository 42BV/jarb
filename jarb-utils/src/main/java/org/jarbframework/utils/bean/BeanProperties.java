package org.jarbframework.utils.bean;

import static org.jarbframework.utils.Asserts.notNull;
import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * Provides support when dealing with bean properties.
 *
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
 */
public final class BeanProperties {
    public static Set<String> getPropertyNames(Class<?> beanClass) {
        Set<String> propertyNames = new HashSet<String>();
        for (PropertyDescriptor propertyDescriptor : getPropertyDescriptors(beanClass)) {
            propertyNames.add(propertyDescriptor.getName());
        }
        propertyNames.addAll(collectFieldNames(beanClass));
        return propertyNames;
    }

    private static Set<String> collectFieldNames(Class<?> beanClass) {
        final Set<String> fieldNames = new HashSet<String>();
        ReflectionUtils.doWithFields(beanClass, new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                fieldNames.add(field.getName());
            }
        });
        return fieldNames;
    }

    public static PropertyReference lastPropertyIn(PropertyReference propertyReference) {
        if (propertyReference.isNestedProperty()) {
            Class<?> parentType = getPropertyType(propertyReference.getParent());
            propertyReference = new PropertyReference(parentType, propertyReference.getSimpleName());
        }
        return propertyReference;
    }

    public static Class<?> getPropertyType(PropertyReference propertyReference) {
        return findPropertyField(propertyReference).getType();
    }

    public static Class<?> getDeclaringClass(PropertyReference propertyReference) {
        return findPropertyField(propertyReference).getDeclaringClass();
    }

    public static Field findPropertyField(PropertyReference propertyReference) {
        propertyReference = lastPropertyIn(propertyReference);
        Field field = ReflectionUtils.findField(propertyReference.getBeanClass(), propertyReference.getName());
        return notNull(field, "Could not find field '" + propertyReference.getName() + "' in '" + propertyReference.getBeanClass().getName() + "'.");
    }

    private BeanProperties() {
    }

}
