package nl._42.jarb.utils.bean;

import static nl._42.jarb.utils.Asserts.state;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Searches for annotations in class and property declarations. 
 * 
 * @author Jeroen van Schagen
 * @since Aug 29, 2011
 */
public class Annotations {

    private Annotations() {
    }

    /**
     * Check if a specific annotation is declared on the class.
     * 
     * @param beanClass bean that should contain the annotation
     * @param annotationType type of annotations that should be looked for
     * @return {@code true} if an annotation could be found, else {@code false}
     */
    public static boolean hasAnnotation(Class<?> beanClass, Class<? extends Annotation> annotationType) {
        return AnnotationUtils.findAnnotation(beanClass, annotationType) != null;
    }

    /**
     * Find a specific annotation on the property declaration.
     * @param propertyReference property that should contain the annotation
     * @return desired annotation, as declared on the property, if any
     */
    public static Collection<Annotation> getAnnotations(PropertyReference propertyReference) {
        PropertyReference finalReference = Beans.getFinalProperty(propertyReference);
        Field field = findField(finalReference);
        Method getter = findReadMethod(finalReference);

        Set<Annotation> annotations = new HashSet<>();
        annotations.addAll(Arrays.asList(getAnnotations(field)));
        annotations.addAll(Arrays.asList(getAnnotations(getter)));
        return annotations;
    }
    
    public static <T extends Annotation> T findAnnotation(PropertyReference propertyReference, Class<T> annotationType) {
        Collection<T> annotations = getAnnotations(propertyReference, annotationType);
        if (annotations.isEmpty()) {
            return null;
        } else {
            state(annotations.size() == 1, "Found more than one @" + annotationType + " on property: " + propertyReference);
            return annotations.iterator().next();
        }
    }

    /**
     * Find a specific annotation on the property declaration.
     * @param propertyReference property that should contain the annotation
     * @param annotationType type of annotation that should be looked for
     * @param <T> annotation type of instance
     * @return desired annotation, as declared on the property, if any
     */
    public static <T extends Annotation> Collection<T> getAnnotations(PropertyReference propertyReference, Class<T> annotationType) {
        PropertyReference finalReference = Beans.getFinalProperty(propertyReference);
        Field field = findField(finalReference);
        Method getter = findReadMethod(finalReference);

        Set<T> annotations = new HashSet<T>();
        addIfNotNull(annotations, findAnnotation(field, annotationType));
        addIfNotNull(annotations, findAnnotation(getter, annotationType));
        return annotations;
    }

    private static Method findReadMethod(PropertyReference propertyReference) {
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(propertyReference.getBeanClass(), propertyReference.getPropertyName());
        
        Method getter = null;
        if (propertyDescriptor != null) {
            getter = propertyDescriptor.getReadMethod();
        }
        return getter;
    }
    
    private static Field findField(PropertyReference propertyReference) {
        return ReflectionUtils.findField(propertyReference.getBeanClass(), propertyReference.getPropertyName());
    }
    
    private static Annotation[] getAnnotations(Method method) {
        if (method == null) {
            return new Annotation[0];
        }
        return AnnotationUtils.getAnnotations(method);
    }

    private static <T extends Annotation> T findAnnotation(Method method, Class<T> annotationType) {
        if (method == null) {
            return null;
        }
        return AnnotationUtils.findAnnotation(method, annotationType);
    }

    private static <T extends Annotation> T findAnnotation(Field field, Class<T> annotationType) {
        if (field == null) {
            return null;
        }
        return field.getAnnotation(annotationType);
    }
    
    private static Annotation[] getAnnotations(Field field) {
        if (field == null) {
            return new Annotation[0];
        }
        return field.getAnnotations();
    }
    
    private static <T> void addIfNotNull(Collection<T> collection, T element) {
        if (element != null) {
            collection.add(element);
        }
    }

    /**
     * Check if a specific property is declared on the property.
     * 
     * @param propertyReference property that should contain the annotation
     * @param annotationType type of annotations that should be looked for
     * @return {@code true} if an annotation could be found, else {@code false}
     */
    public static boolean hasAnnotation(PropertyReference propertyReference, Class<? extends Annotation> annotationType) {
        return !getAnnotations(propertyReference, annotationType).isEmpty();
    }

}
