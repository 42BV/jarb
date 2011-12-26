package org.jarbframework.populator;

/**
 * Update on the database.
 * @author Jeroen van Schagen
 * @since 20-06-2011
 */
public interface DatabaseUpdater {

    /**
     * Perform an update on the database. There are many types of database updates, for example:
     * <ul>
     *  <li>Inserting data</li>
     *  <li>Modifying data</li>
     *  <li>Altering the schema</li>
     * </ul>
     */
    void update();

}
