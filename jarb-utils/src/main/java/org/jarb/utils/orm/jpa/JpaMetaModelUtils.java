package org.jarb.utils.orm.jpa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarb.utils.BeanAnnotationUtils;
import org.jarb.utils.orm.NotAnEntityException;

/**
 * Table mapper that works using Java Persistence API (JPA) annotations.
 * 
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public class JpaMetaModelUtils {

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

    /**
     * Determine if an entity has a parent entity.
     * @param entityClass class of the entity to check
     * @return {@code true} if a parent entity was found, else {@code false}
     */
    private static boolean hasEntitySuperclass(Class<?> entityClass) {
        Class<?> currentClass = entityClass;
        while (currentClass.getSuperclass() != null) {
            currentClass = currentClass.getSuperclass();
            if (isEntity(currentClass)) {
                return true;
            }
        }
        return false;
    }

    public static String getIdentifierPropertyName(Class<?> entityClass) {
        return BeanAnnotationUtils.findPropertyWithAnnotation(entityClass, Id.class);
    }

    public static Class<?> getRootEntityClass(Class<?> entityClass) {
        assertIsEntity(entityClass);

        Class<?> currentClass = entityClass;
        while (currentClass.getSuperclass() != null) {
            currentClass = currentClass.getSuperclass();
            if (isEntity(currentClass)) {
                entityClass = currentClass;
            }
        }
        return entityClass;
    }

    public static void assertIsEntity(Class<?> beanClass) {
        if (!isEntity(beanClass)) {
            throw new NotAnEntityException("Bean class '" + beanClass.getName() + "' is not an @Entity.");
        }
    }

}
