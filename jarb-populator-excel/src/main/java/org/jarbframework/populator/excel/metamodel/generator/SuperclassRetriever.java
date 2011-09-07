package org.jarbframework.populator.excel.metamodel.generator;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.metamodel.EntityType;

/**
 * Holds functions to retrieve superclasses based on an EntityType from the metamodel.
 * @author Sander Benschop
 *
 */
public final class SuperclassRetriever {

    /** Private constructor. */
    private SuperclassRetriever() {
    }

    /**
      * Checks if the subEntity can find its super class in the complete list of entities.
      * @param subEntity The subentity of the entity that is being sought.
      * @param entities A list with all entities from the metamodel
      * @return Superclass entity
      */
    public static EntityType<?> getSuperClassEntity(EntityType<?> subEntity, Set<EntityType<?>> entities) {
        if (entities == null) {
            return null;
        }
        for (EntityType<?> entity : entities) {
            if (RelationshipCheckFunctions.isSubClassOf(subEntity, entity)) {
                return entity;
            }
        }

        return null;
    }

    /**
     * Checks if the peristent class has a superclass, and if it does it checks if this superclass also has one, and so on.
     * Creates a list of all superclasses in the levels above the persistentclass except for java.lang.Object which is of no use to us
     * @param persistentClass Lowest level class
     * @return List of classes
     */
    public static Set<Class<?>> getListOfSuperClasses(Class<?> persistentClass) {
        Set<Class<?>> superClasses = new HashSet<Class<?>>();
        Class<?> current = persistentClass;
        while (current != null) {
            current = addSuperclassAndReturnNewClassToCheck(superClasses, current);
        }
        return superClasses;
    }

    /**
     * Checks if the current class holds a valid superclass and adds this.
     * Returns a new current class which is one level higher
     * @param superClasses Set of superclass to add data to
     * @param current Current class that is being checked
     * @return New current class
     */
    private static Class<?> addSuperclassAndReturnNewClassToCheck(Set<Class<?>> superClasses, Class<?> current) {
        if ((current.getSuperclass() != null) && (current.getSuperclass() != java.lang.Object.class)) {
            current = getSuperclass(superClasses, current);
        } else {
            current = null;
        }
        return current;
    }

    /**
     * Returns the superclass to the list. Then returns new current class.
     * @param superClasses Set of superclasses
     * @param current Class that is currently being checked
     * @return Returns new current class
     */
    private static Class<?> getSuperclass(Set<Class<?>> superClasses, Class<?> current) {
        Class<?> superclass = current.getSuperclass();
        superClasses.add(superclass);
        current = superclass;
        return current;
    }
}
