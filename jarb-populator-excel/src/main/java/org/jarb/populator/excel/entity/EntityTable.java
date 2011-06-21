package org.jarb.populator.excel.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * In-memory representation of a database table. Contains all
 * entities of a specific type, allowing them to be retrieved.
 * 
 * @author Jeroen van Schagen
 * @since 09-05-2011
 * 
 * @param <T> type of entities being maintained
 */
public class EntityTable<T> implements Iterable<T> {
    private final Class<T> entityClass;
    private Map<Object, T> entitiesMap = new HashMap<Object, T>();
    
    public EntityTable(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
    
    /**
     * Retrieve an entity based on its identifier.
     * @param id identifier of the entity
     * @return entity matching or identifier, if any
     */
    public T get(Object id) {
        return entitiesMap.get(id);
    }

    /**
     * Retrieve every entity, in {@link List} format.
     * @return list representation of each stored entity
     */
    public List<T> list() {
        return new ArrayList<T>(entitiesMap.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return list().iterator();
    }

    /**
     * Retrieve every entity, in {@link Map} format.
     * @return map representation of each stored entity
     */
    public Map<Object, T> map() {
        return Collections.unmodifiableMap(entitiesMap);
    }

    /**
     * Count the number of stored entities.
     * @return number of stored entities
     */
    public int count() {
        return entitiesMap.size();
    }

    /**
     * Check if there are no stored entites.
     * @return {@code true} if the table is empty, else {@code false}
     */
    public boolean isEmpty() {
        return entitiesMap.isEmpty();
    }

    /**
     * Store a specific entity inside this object.
     * @param id identifier of the entity
     * @param entity reference to the entity
     */
    public void add(Object id, T entity) {
        entitiesMap.put(id, entity);
    }

    /**
     * Remove a specific entity.
     * @param id identifier of the entity
     * @return removed entity
     */
    public T remove(Object id) {
        return entitiesMap.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return entitiesMap.toString();
    }

}
