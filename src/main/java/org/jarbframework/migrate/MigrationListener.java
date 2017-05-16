/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.migrate;

/**
 * Listener invoked after migration.
 * 
 * @author Jeroen van Schagen
 * @since Apr 10, 2014
 */
public interface MigrationListener {
    
    /**
     * Callback invoked after migration.
     */
    void afterMigrate();
    
}
