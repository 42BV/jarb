package org.jarb.populator.excel.util;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

/**
 * Java Persistence API (JPA) utilities.
 * 
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public final class JpaUtils {

    /**
     * Create a new {@link EntityManager}, using all configured properties.
     * @param entityManagerFactory factory that builds entity managers
     * @return new entity manager instance
     */
    public static EntityManager createEntityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager(entityManagerFactory.getProperties());
    }

    /**
     * Retrieve the {@link Class} of each entity type known inside our persistence context.
     * @param entityManagerFactory holds our persistence context
     * @return set of all entity classes in our context
     */
    public static Set<Class<?>> getEntityClasses(EntityManagerFactory entityManagerFactory) {
        Set<Class<?>> entityClasses = new HashSet<Class<?>>();
        Metamodel jpaMetamodel = entityManagerFactory.getMetamodel();
        for (EntityType<?> entityType : jpaMetamodel.getEntities()) {
            entityClasses.add(entityType.getJavaType());
        }
        return entityClasses;
    }
    
    /**
     * Retrieve the identifier (@Id) value of an entity.
     * @param entity reference to the entity
     * @param entityManagerFactory entity manager factory
     * @return identifier of the entity, if any
     */
    public static Object getIdentifier(Object entity, EntityManagerFactory entityManagerFactory) {
        PersistenceUnitUtil persistenceUtil = entityManagerFactory.getPersistenceUnitUtil();
        return persistenceUtil.getIdentifier(entity);
    }
    
}
