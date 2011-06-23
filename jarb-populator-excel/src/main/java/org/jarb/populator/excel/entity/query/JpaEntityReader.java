package org.jarb.populator.excel.entity.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.entity.EntityTable;
import org.jarb.populator.excel.util.JpaUtils;

/**
 * Java Persistence API (JPA) implementation of {@link EntityReader}.
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public class JpaEntityReader implements EntityReader {
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    /**
     * Construct a new {@link JpaEntityReader}.
     * @param entityManagerFactory entity manager factory used for retrieving data
     */
    public JpaEntityReader(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        entityManager = JpaUtils.createEntityManager(entityManagerFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegistry fetchAll() {
        return fetchForTypes(JpaUtils.getEntityClasses(entityManagerFactory));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegistry fetchForTypes(Iterable<Class<?>> entityClasses) {
        EntityRegistry registry = new EntityRegistry();
        for (Class<?> entityClass : entityClasses) {
            registry.addAll(fetchForType(entityClass));
        }
        return registry;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> EntityTable<T> fetchForType(Class<T> entityClass) {
        EntityTable<T> entities = new EntityTable<T>(entityClass);
        for (T entity : queryForClass(entityClass)) {
            entities.add(JpaUtils.getIdentifier(entity, entityManagerFactory), entity);
        }
        return entities;
    }
    
    /**
     * Perform the actual database query, retrieving our entities.
     * @param <T> type of entities to retrieve
     * @param entityClass class reference to the entity type
     * @return list of each entity, currently stored inside the database, from that type
     */
    protected <T> List<T> queryForClass(Class<T> entityClass) {
        EntityManager entityManager = JpaUtils.createEntityManager(entityManagerFactory);
        return entityManager.createQuery("from " + entityClass.getName(), entityClass).getResultList();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T fetch(Class<T> entityClass, Object identifier) {
        return entityManager.find(entityClass, identifier);
    }



}
