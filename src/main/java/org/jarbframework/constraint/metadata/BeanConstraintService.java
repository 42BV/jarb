package org.jarbframework.constraint.metadata;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import org.jarbframework.utils.ClassScanner;

/**
 * Created by maarten.hus on 22/05/15.
 */
public class BeanConstraintService {

    private final Set<String> ignoredProperties = new HashSet<>();
    
    private final Set<Class<?>> entityClasses = new HashSet<>();

    private final BeanConstraintDescriptor beanConstraintDescriptor;
    
    {
        ignoredProperties.add("new");
        ignoredProperties.add("id");
        ignoredProperties.add("class");
    }

    public BeanConstraintService(BeanConstraintDescriptor beanConstraintDescriptor) {
        this.beanConstraintDescriptor = beanConstraintDescriptor;
    }

    /**
     * Retrieves all entities registered.
     * @return the descriptions
     */
    public Map<String, Map<String, PropertyConstraintDescription>> describeAll() {
        Map<String, Map<String, PropertyConstraintDescription>> descriptions = new HashMap<>();
        for (Class<?> entityClass : entityClasses) {
            Map<String, PropertyConstraintDescription> properties = describe(entityClass);
            descriptions.put(getEntityName(entityClass), properties);
        }
        return descriptions;
    }

    protected String getEntityName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }
    
    /**
     * Describes a certain entity.
     * @param entityClass the entity class
     * @return the description
     */
    public Map<String, PropertyConstraintDescription> describe(Class<?> entityClass) {
        BeanConstraintDescription description = beanConstraintDescriptor.describeBean(entityClass);
        Map<String, PropertyConstraintDescription> properties = new HashMap<>(description.getProperties());
        ignoredProperties.stream().forEach(properties::remove);
        return properties;
    }


    public void registerClass(Class<?> entityClass) {
        entityClasses.add(entityClass);
    }
    
    public void registerAllWithAnnotation(Class<?> basePackageClass, Class<? extends Annotation> annotationClass) {
        String basePackage = basePackageClass.getPackage().getName();
        registerAllWithAnnotation(basePackage, annotationClass);
    }
    
    public void registerAllWithAnnotation(String basePackage, Class<? extends Annotation> annotationClass) {
        Set<Class<?>> entityClasses = ClassScanner.getAllWithAnnotation(basePackage, Entity.class);
        for (Class<?> entityClass : entityClasses) {
            registerClass(entityClass);
        }
    }

    public Set<String> getIgnoredProperties() {
        return ignoredProperties;
    }

}