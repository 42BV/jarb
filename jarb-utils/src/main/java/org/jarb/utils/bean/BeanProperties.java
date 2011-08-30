/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.bean;

import static org.jarb.utils.Conditions.notNull;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * Provides support when dealing with bean properties.
 *
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
 */
public abstract class BeanProperties {

    public static Set<String> getPropertyNames(Class<?> beanClass) {
        Set<String> propertyNames = new HashSet<String>();
        for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(beanClass)) {
            propertyNames.add(propertyDescriptor.getName());
        }
        propertyNames.addAll(collectFieldNames(beanClass));
        return propertyNames;
    }

    private static Set<String> collectFieldNames(Class<?> beanClass) {
        final Set<String> fieldNames = new HashSet<String>();
        org.springframework.util.ReflectionUtils.doWithFields(beanClass, new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                fieldNames.add(field.getName());
            }
        });
        return fieldNames;
    }

    public static Class<?> getPropertyType(Class<?> beanClass, String propertyName) {
        Field field = org.springframework.util.ReflectionUtils.findField(beanClass, propertyName);
        return notNull(field, "Could not find property '" + propertyName + "' in " + beanClass.getName()).getType();
    }

    public static Class<?> getDeclaringClass(Class<?> beanClass, String propertyName) {
        Field field = org.springframework.util.ReflectionUtils.findField(beanClass, propertyName);
        return notNull(field, "Could not find property '" + propertyName + "' in " + beanClass.getName()).getDeclaringClass();
    }

}
