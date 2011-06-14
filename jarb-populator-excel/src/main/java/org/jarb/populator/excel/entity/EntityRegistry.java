package org.jarb.populator.excel.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Capable of storing and retrieving entities from any type.
 * 
 * @author Jeroen van Schagen
 * @since 09-05-2011
 */
public class EntityRegistry {
    private Map<Class<?>, EntityTable<?>> entityTables = new HashMap<Class<?>, EntityTable<?>>();

    /**
     * Retrieve the classes of each entity type stored in this registry.
     * @return collection of each entity type
     */
    public Set<Class<?>> getEntityClasses() {
        return Collections.unmodifiableSet(entityTables.keySet());
    }

    /**
     * Retrieve a specific entity.
     * @param <T> type of entity being retrieved
     * @param entityClass class of the entity being retrieved
     * @param id identifier of the entity being retrieved
     * @return entity of the specified type and identity, if any
     */
    public <T> T get(Class<T> entityClass, Long id) {
        return entityTable(entityClass).get(id);
    }

    /**
     * Retrieve all entities of a specific type.
     * @param <T> type of the entities being retrieved
     * @param entityClass class of the entities
     * @return each entity of the specified type
     */
    public <T> EntityTable<T> getAll(Class<T> entityClass) {
        return entityTable(entityClass);
    }

    /**
     * Determine if a specific entity exists in our registry.
     * @param entityClass class of the entity
     * @param id identifier of the entity
     * @return {@code true} if it exists, else {@code false}
     */
    public boolean exists(Class<?> entityClass, Long id) {
        return get(entityClass, id) != null;
    }

    /**
     * Store an entity inside this registry.
     * @param <T> type of the entity being stored
     * @param entityClass class that the entity should be registered on
     * @param id identifier of the entity
     * @param entity reference to the entity being stored
     */
    public <T> void add(Class<T> entityClass, Long id, T entity) {
        entityTable(entityClass).add(id, entity);
    }

    /**
     * Store a map of entities inside this registry.
     * @param <T> type of the entities being stored
     * @param entities references to each entity that should be stored
     */
    public <T> void addAll(EntityTable<T> entities) {
        EntityTable<T> currentEntities = entityTable(entities.getEntityClass());
        for (Map.Entry<Long, T> entityEntry : entities.map().entrySet()) {
            currentEntities.add(entityEntry.getKey(), (T) entityEntry.getValue());
        }
    }
    
    /**
     * Remove a specific entity from our registry.
     * @param <T> type of the entity being removed
     * @param entityClass class of the entity
     * @param id identifier of the entity
     * @return the removed entity
     */
    public <T> T remove(Class<T> entityClass, Long id) {
        return entityTable(entityClass).remove(id);
    }

    /**
     * Remove a collection of entities from our registry.
     * @param <T> type of the entities being removed
     * @param entityClass class of the entities
     * @param ids identifiers of the entities
     * @return the removed entities
     */
    public <T> List<T> removeAll(Class<T> entityClass, Iterable<Long> ids) {
        final List<T> deletedEntities = new ArrayList<T>();
        EntityTable<T> entities = entityTable(entityClass);
        for (Long id : ids) {
            T deletedEntity = entities.remove(id);
            if (deletedEntity != null) {
                deletedEntities.add(deletedEntity);
            }
        }
        return deletedEntities;
    }

    /**
     * Retrieve the holder that maintains entities of a specific type.
     * @param <T> type of entities
     * @param entityClass class of the entities
     * @return holder of our entities
     */
    @SuppressWarnings("unchecked")
    private <T> EntityTable<T> entityTable(Class<T> entityClass) {
        EntityTable<T> entities = (EntityTable<T>) entityTables.get(entityClass);
        if (entities == null) {
            entities = new EntityTable<T>(entityClass);
            entityTables.put(entityClass, entities);
        }
        return entities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return entityTables.toString();
    }
}
