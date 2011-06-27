package org.jarb.populator.excel.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.proxy.HibernateProxy;

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
     * Retrieve the identifier (@Id) value of an entity.
     * @param entity reference to the entity
     * @param entityManagerFactory entity manager factory
     * @return identifier of the entity, if any
     */
    public static Object getIdentifier(Object entity, EntityManagerFactory entityManagerFactory) {
        if (entity instanceof HibernateProxy) {
            entity = ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entityManagerFactory.getPersistenceUnitUtil().getIdentifier(entity);
    }
    
}
