package org.jarbframework.utils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

public class Classes {

    @SuppressWarnings("unchecked")
    public static <T> Class<T> forName(String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean hasPackage(String packageName) {
        return Package.getPackage(packageName) != null;
    }

    public static boolean isOnClasspath(String className) {
        try {
            Class.forName(className);
            return true;
        } catch(ClassNotFoundException e) {
            return false;
        }
    }
    
    public static Set<Class<?>> getAllOfType(String basePackage, Class<?> targetType) {
        return getAll(basePackage, new AssignableTypeFilter(targetType));
    }
    
    public static Set<Class<?>> getAllWithAnnotation(String basePackage, Class<? extends Annotation> annotationClass) {
        return getAll(basePackage, new AnnotationTypeFilter(annotationClass));
    }
        
    private static Set<Class<?>> getAll(String basePackage, TypeFilter filter) {
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(false);
        componentProvider.addIncludeFilter(filter);
        Set<BeanDefinition> beanDefinitions = componentProvider.findCandidateComponents(basePackage);
        Set<Class<?>> beanClasses = new HashSet<Class<?>>(beanDefinitions.size());
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> beanClass = forName(beanDefinition.getBeanClassName());
            beanClasses.add(beanClass);
        }
        return beanClasses;
    }
    
}
