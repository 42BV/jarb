package org.jarb.populator;

/**
 * Populate the database with data.
 * 
 * @author Jeroen van Schagen
 * @since 20-06-2011
 */
public interface DatabasePopulator {

    /**
     * Populate database with data.
     * @throws Exception whenever an unrecoverable fault
     * occured during database populating.
     */
    void populate() throws Exception;

}
