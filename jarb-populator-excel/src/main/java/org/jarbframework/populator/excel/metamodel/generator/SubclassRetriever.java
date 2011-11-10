package org.jarbframework.populator.excel.metamodel.generator;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.metamodel.EntityType;

/**
 * Class with functions in it that can be used to retrieve sub- and superclasses.
 * @author Sander Benschop
 *
 */
public final class SubclassRetriever {

    /** Private constructor. */
    private SubclassRetriever() {
    }

    /**
     * Returns a set with the entity's subclasses.
     * @param superEntity SuperEntity
     * @param entities A list with all entities from the metamodel
     * @return Set of Entity's subclass entities
     */
    public static Set<EntityType<?>> getSubClassEntities(EntityType<?> superEntity, Set<EntityType<?>> entities) {
        Set<EntityType<?>> subEntities = new HashSet<EntityType<?>>();
        if (entities != null) {
            for (EntityType<?> entity : entities) {
                subEntities.addAll(addSubclassEntities(superEntity, entities, entity));
            }
        }
        return subEntities;
    }

    /**
     * Returns a mapping with all subclasses in it, the discriminator value is the key.
     * @param subClassEntities Subclass entities of the Entity
     * @return Map with discriminator values and persistent classes.
     */
    public static Map<String, Class<?>> getSubClassMapping(Set<EntityType<?>> subClassEntities) {
        Map<String, Class<?>> subClassMap = new HashMap<String, Class<?>>();
        for (EntityType<?> subClassEntity : subClassEntities) {
            Class<?> subClass = subClassEntity.getJavaType();
            String discriminatorValue = getDiscriminatorValue(subClass);
            subClassMap.put(discriminatorValue, subClass);
        }
        return subClassMap;
    }

    /**
     * Returns the class' discriminator value, the default is the Simple Classname.
     * This can be overriden by using the @DiscriminatorValue annotation.
     * If an @Entity(name="") annotation is present, this will be used because Hibernate does this too
     * @param definedClass Persistent class
     * @return The persistent class' discriminator value
     */
    public static String getDiscriminatorValue(Class<?> persistentClass) {
        String returnValue = tryToReadDiscriminatorValueAnnotation(persistentClass);
        if (returnValue != null) {
            return returnValue;
        }
        returnValue = tryToReadEntityNameAnnotationForDiscriminatorValue(persistentClass);
        if (returnValue != null) {
            return returnValue;
        }
        return persistentClass.getSimpleName();
    }

    /**
     * Tries to read the discriminator value from the @DiscriminatorValue anottation from the persistent class.
     * @param definedClass Persistent class to read annotation from
     * @return Discriminator value or null if annotation cannot be read or found.
     */
    private static String tryToReadDiscriminatorValueAnnotation(Class<?> persistentClass) {
        for (Annotation annotation : persistentClass.getAnnotations()) {
            if (annotation.annotationType().equals(DiscriminatorValue.class)) {
                javax.persistence.DiscriminatorValue discriminatorAnnotation = (javax.persistence.DiscriminatorValue) annotation;
                return discriminatorAnnotation.value();
            }
        }
        return null;
    }

    /**
     * Loops over all the annotations in the persistent class to find an Entity annotation.
     * Calls getentityNameFromEntityAnnotation function to retrieve entity name value and returns this.
     * @param definedClass Persistent class to read annotation from
     * @return Discriminator value or null if annotation cannot be read or found.
     */
    private static String tryToReadEntityNameAnnotationForDiscriminatorValue(Class<?> persistentClass) {
        for (Annotation annotation : persistentClass.getAnnotations()) {
            if (annotation.annotationType().equals(Entity.class)) {
                return getEntityNameFromEntityAnnotation(annotation);
            }
        }
        return null;
    }

    /**
     * Tries to read the @Entity(name="") annotation from a class to determine the discriminator value.
     * @param annotation Annotation to read from
     * @return Entity name value or null if not present
     */
    private static String getEntityNameFromEntityAnnotation(Annotation annotation) {
        Entity annotatedEntity = (Entity) annotation;
        if ((annotatedEntity.name() != null) && (!annotatedEntity.name().isEmpty())) {
            return annotatedEntity.name();
        }
        return null;
    }

    /**
     * Adds a subclass entity to the set of EntityType<?> if this is actually the case and adds the subclasses of this subclass too if available.
     * @param superEntity Super entity from the JPA metamodel
     * @param entities All entities from the metamodel
     * @param entity Entity that might be a subclass
     * @return Set of Entities.
     */
    private static Set<EntityType<?>> addSubclassEntities(EntityType<?> superEntity, Set<EntityType<?>> entities, EntityType<?> entity) {
        Set<EntityType<?>> subEntities = new HashSet<EntityType<?>>();
        if (RelationshipCheckFunctions.isSubClassOf(entity, superEntity)) {
            subEntities.add(entity);
            subEntities.addAll(getSubClassEntities(entity, entities));
        }
        return subEntities;
    }
}
