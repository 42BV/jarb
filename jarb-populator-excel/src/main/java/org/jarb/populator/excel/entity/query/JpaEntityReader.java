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

    public JpaEntityReader(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegistry fetchAll() {
        return fetch(JpaUtils.getEntityClasses(entityManagerFactory));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityRegistry fetch(Iterable<Class<?>> entityClasses) {
        EntityRegistry registry = new EntityRegistry();
        for (Class<?> entityClass : entityClasses) {
            registry.addAll(fetchEntitiesWithIdentifier(entityClass));
        }
        return registry;
    }

    /**
     * Fetch all entities of a specific type.
     * @param classDefinition describes the type of entity to retrieve
     * @return map of each retrieved entity
     */
    private <T> EntityTable<T> fetchEntitiesWithIdentifier(Class<T> entityClass) {
        EntityTable<T> entities = new EntityTable<T>(entityClass);
        for (T entity : fetchEntities(entityClass)) {
            entities.add(JpaUtils.getIdentifierAsLong(entity, entityManagerFactory), entity);
        }
        return entities;
    }

    /**
     * Perform the actual database query, retrieving our entities.
     * @param <T> type of entities to retrieve
     * @param entityClass class reference to the entity type
     * @return list of each entity, currently stored inside the database, from that type
     */
    protected <T> List<T> fetchEntities(Class<T> entityClass) {
        EntityManager entityManager = JpaUtils.createEntityManager(entityManagerFactory);
        return entityManager.createQuery("from " + entityClass.getName(), entityClass).getResultList();
    }

}
