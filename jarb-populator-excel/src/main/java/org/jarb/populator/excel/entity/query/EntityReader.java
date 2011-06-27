package org.jarb.populator.excel.entity.query;

import org.jarb.populator.excel.entity.EntityRegistry;
import org.jarb.populator.excel.entity.EntityTable;

/**
 * Capable of retrieving entity instances from the database.
 * 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public interface EntityReader {

    /**
     * Retrieve all entities from the database.
     * @return registry containing every entities
     */
    EntityRegistry readAll();
    
    /**
     * Retrieve all entities of a specific type from the database.
     * @param <T> type of entities
     * @param entityClass class of the entity type
     * @return entity table containing all entities of that type
     */
    <T> EntityTable<T> readFrom(Class<T> entityClass);
    
    /**
     * Retrieve a specific entity from the database.
     * @param <T> type of entity
     * @param entityClass entity class
     * @param identifier entity identifier
     * @return entity, if any, else {@code null}
     */
    <T> T read(Class<T> entityClass, Object identifier);

}
