package org.jarb.utils.orm.jpa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarb.utils.orm.NotAnEntityException;

/**
 * Table mapper that works using Java Persistence API (JPA) annotations.
 * 
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public class JpaMetaModelUtils {

    public static void assertIsEntity(Class<?> beanClass) {
        if (!isEntity(beanClass)) {
            throw new NotAnEntityException("Bean class '" + beanClass.getName() + "' is not an @Entity.");
        }
    }

    /**
     * Determine if a bean class is annotated with @Entity.
     * @param beanClass class of the bean
     * @return {@code true} if it is annotated, else {@code false}
     */
    public static boolean isEntity(Class<?> beanClass) {
        return beanClass.getAnnotation(Entity.class) != null;
    }

    /**
     * Determine if a bean class is annotated with @Embeddable.
     * @param beanClass class of the bean
     * @return {@code true} if it is annotated, else {@code false}
     */
    public static boolean isEmbeddable(Class<?> beanClass) {
        return beanClass.getAnnotation(Embeddable.class) != null;
    }

    /**
     * Retrieve all "root" entities described in our JPA meta-model.
     * @param metamodel JPA meta-model, containing all entity descriptions
     * @return entity types for each root entity class
     */
    public static Collection<EntityType<?>> getRootEntities(Metamodel metamodel) {
        Set<EntityType<?>> entityTypes = new HashSet<EntityType<?>>();
        for (EntityType<?> entityType : metamodel.getEntities()) {
            if (!hasEntitySuperclass(entityType.getJavaType())) {
                entityTypes.add(entityType);
            }
        }
        return entityTypes;
    }

    private static boolean hasEntitySuperclass(Class<?> entityClass) {
        return findParentEntityClass(entityClass) != null;
    }

    public static Class<?> findParentEntityClass(Class<?> beanClass) {
        Class<?> entityClass = null;
        Class<?> currentClass = beanClass;
        while (currentClass.getSuperclass() != null) {
            currentClass = currentClass.getSuperclass();
            if (isEntity(currentClass)) {
                entityClass = currentClass;
                break;
            }
        }
        return entityClass;
    }

    public static Class<?> findRootEntityClass(Class<?> beanClass) {
        Class<?> entityClass = null;
        Class<?> currentClass = beanClass;
        do {
            if (isEntity(currentClass)) {
                entityClass = currentClass;
            }
        } while ((currentClass = currentClass.getSuperclass()) != null);
        return entityClass;
    }

}
