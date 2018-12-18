package nl._42.jarb.constraint.metadata;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl._42.jarb.utils.ClassScanner;

/**
 * Created by maarten.hus on 22/05/15.
 */
public class BeanConstraintService {

    private final Set<String> ignoredProperties = new HashSet<>();
    
    private final Set<Class<?>> beanTypes = new HashSet<>();

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
        for (Class<?> beanType : beanTypes) {
            Map<String, PropertyConstraintDescription> properties = describe(beanType);
            descriptions.put(getTypeName(beanType), properties);
        }
        return descriptions;
    }

    protected String getTypeName(Class<?> beanType) {
        return beanType.getSimpleName();
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

    /**
     * Registers a class.
     * @param beanType the bean type
     */
    public void registerClass(Class<?> beanType) {
        beanTypes.add(beanType);
    }
    
    /**
     * Registers all classes in a package with an annotation.
     * @param basePackageClass the base package class
     * @param annotationClass the annotation type
     */
    public void registerAllWithAnnotation(Class<?> basePackageClass, Class<? extends Annotation> annotationClass) {
        String basePackage = basePackageClass.getPackage().getName();
        registerAllWithAnnotation(basePackage, annotationClass);
    }
    
    /**
     * Registers all classes in a package with an annotation.
     * @param basePackage the base package
     * @param annotationClass the annotation type
     */
    public void registerAllWithAnnotation(String basePackage, Class<? extends Annotation> annotationClass) {
        Set<Class<?>> beanTypes = ClassScanner.getAllWithAnnotation(basePackage, annotationClass);
        beanTypes.forEach(beanType -> registerClass(beanType));
    }

    /**
     * Retrieves all ignored properties.
     * @return the ignored properties
     */
    public Set<String> getIgnoredProperties() {
        return ignoredProperties;
    }

}