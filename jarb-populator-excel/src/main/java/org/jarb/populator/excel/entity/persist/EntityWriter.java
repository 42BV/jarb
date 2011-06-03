package org.jarb.populator.excel.entity.persist;

import org.jarb.populator.excel.entity.EntityRegistry;

/**
 * Persist entities in the database. 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public interface EntityWriter {

    /**
     * Persist entities in the database.
     * @param registry the entities to persist
     */
    void persist(EntityRegistry registry);

}
