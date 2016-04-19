package org.jarbframework.utils.bean;

import static org.jarbframework.utils.Asserts.notNull;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * Provides support when dealing with bean properties.
 *
 * @author Jeroen van Schagen
 * @since Aug 29, 2011
 */
public final class Beans {

    private Beans() {
    }

    public static Set<String> getFieldNames(Class<?> beanClass) {
        final Set<String> fieldNames = new HashSet<String>();
        ReflectionUtils.doWithFields(beanClass, new FieldCallback() {
        	
            @Override
            public void doWith(Field field) throws IllegalAccessException {
                fieldNames.add(field.getName());
            }
            
        });
        return fieldNames;
    }

    public static PropertyReference getFinalProperty(PropertyReference propertyReference) {
        if (propertyReference.isNestedProperty()) {
            Class<?> parentType = findPropertyField(propertyReference.getParent()).getType();
            propertyReference = new PropertyReference(parentType, propertyReference.getSimpleName());
        }
        return propertyReference;
    }
    
    public static Field findPropertyField(PropertyReference propertyReference) {
        PropertyReference finalReference = getFinalProperty(propertyReference);
        Field field = ReflectionUtils.findField(finalReference.getBeanClass(), finalReference.getPropertyName());
        return notNull(field, "Could not find field '" + finalReference.getPropertyName() + "' in '" + finalReference.getBeanClass().getName() + "'.");
    }

}
