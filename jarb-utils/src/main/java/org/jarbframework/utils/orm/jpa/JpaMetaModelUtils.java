package org.jarbframework.utils.orm.jpa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

/**
 * JPA meta model support functionality.
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public final class JpaMetaModelUtils {

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

    private static boolean hasEntitySuperclass(Class<?> beanClass) {
        boolean entitySuperClassFound = false;
        Class<?> currentClass = beanClass;
        while ((currentClass = currentClass.getSuperclass()) != null) {
            if (isEntity(currentClass)) {
                entitySuperClassFound = true;
                break;
            }
        }
        return entitySuperClassFound;
    }

    /**
     * Retrieve the root entity class of a bean.
     * @param beanClass class of the bean
     * @return root entity class, if any could be found
     */
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

    private JpaMetaModelUtils() {
    }

}
