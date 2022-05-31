/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

/**
 * Used for scanning classes.
 *
 * @author Jeroen van Schagen
 * @since Feb 27, 2014
 */
public class ClassScanner {
    
    /**
     * Retrieve all classes with a specific type.
     * 
     * @param basePackage the base package
     * @param targetType the target type
     * @return all matching classes
     */
    public static Set<Class<?>> getAllOfType(String basePackage, Class<?> targetType) {
        return getAll(basePackage, new AssignableTypeFilter(targetType));
    }
    
    /**
     * Retrieve all classes with a specific annotation.
     * 
     * @param basePackage the base package
     * @param annotationType the annotation type
     * @return all matching classes
     */
    public static Set<Class<?>> getAllWithAnnotation(String basePackage, Class<? extends Annotation> annotationType) {
        return getAll(basePackage, new AnnotationTypeFilter(annotationType));
    }
    
    private static Set<Class<?>> getAll(String basePackage, TypeFilter filter) {
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(false);
        componentProvider.addIncludeFilter(filter);
        Set<BeanDefinition> beanDefinitions = componentProvider.findCandidateComponents(basePackage);
        Set<Class<?>> beanClasses = new HashSet<>(beanDefinitions.size());
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> beanClass = Classes.forName(beanDefinition.getBeanClassName());
            beanClasses.add(beanClass);
        }
        return beanClasses;
    }

}
