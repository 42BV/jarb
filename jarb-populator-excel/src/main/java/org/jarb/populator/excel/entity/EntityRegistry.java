package org.jarb.populator.excel.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Capable of storing and retrieving entities from any type.
 * 
 * @author Jeroen van Schagen
 * @since 09-05-2011
 */
public class EntityRegistry implements Iterable<EntityTable<?>> {
    private Map<Class<?>, EntityTable<?>> entityTables = new HashMap<Class<?>, EntityTable<?>>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<EntityTable<?>> iterator() {
        return entityTables.values().iterator();
    }
    
    /**
     * Retrieve all entities of a specific type.
     * @param <T> type of the entities being retrieved
     * @param entityClass class of the entities
     * @return each entity of the specified type
     */
    @SuppressWarnings("unchecked")
    public <T> EntityTable<T> forClass(Class<T> entityClass) {
        EntityTable<T> entities = (EntityTable<T>) entityTables.get(entityClass);
        if (entities == null) {
            entities = new EntityTable<T>(entityClass);
            entityTables.put(entityClass, entities);
        }
        return entities;
    }

    /**
     * Retrieve a specific entity.
     * @param <T> type of entity being retrieved
     * @param entityClass class of the entity being retrieved
     * @param id identifier of the entity being retrieved
     * @return entity of the specified type and identity, if any
     */
    public <T> T find(Class<T> entityClass, Object id) {
        return forClass(entityClass).find(id);
    }

    /**
     * Determine if a specific entity exists in our registry.
     * @param entityClass class of the entity
     * @param id identifier of the entity
     * @return {@code true} if it exists, else {@code false}
     */
    public boolean exists(Class<?> entityClass, Object id) {
        return forClass(entityClass).exists(id);
    }

    /**
     * Store an entity inside this registry.
     * @param <T> type of the entity being stored
     * @param entityClass class that the entity should be registered on
     * @param id identifier of the entity
     * @param entity reference to the entity being stored
     */
    public <T> void add(Class<T> entityClass, Object id, T entity) {
        forClass(entityClass).add(id, entity);
    }

    /**
     * Store a map of entities inside this registry.
     * @param <T> type of the entities being stored
     * @param entities references to each entity that should be stored
     */
    public <T> void addAll(EntityTable<T> entities) {
        EntityTable<T> storedEntities = forClass(entities.getEntityClass());
        for (Map.Entry<Object, T> entityEntry : entities.map().entrySet()) {
            storedEntities.add(entityEntry.getKey(), entityEntry.getValue());
        }
    }
    
    /**
     * Remove a specific entity from our registry.
     * @param <T> type of the entity being removed
     * @param entityClass class of the entity
     * @param id identifier of the entity
     * @return the removed entity
     */
    public <T> T remove(Class<T> entityClass, Object id) {
        return forClass(entityClass).remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return entityTables.toString();
    }
}
