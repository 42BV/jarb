package org.jarbframework.populator.excel.entity.query;

import org.jarbframework.populator.excel.entity.EntityRegistry;

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

}
