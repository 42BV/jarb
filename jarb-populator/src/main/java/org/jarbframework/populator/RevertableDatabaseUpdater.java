package org.jarbframework.populator;

/**
 * Database updater that can have its updates reverted.
 * @author Jeroen van Schagen
 * @since 20-12-2011
 */
public interface RevertableDatabaseUpdater extends DatabaseUpdater {

    /**
     * Revert the {@link #update()} operation.
     */
    void revert();
    
}
