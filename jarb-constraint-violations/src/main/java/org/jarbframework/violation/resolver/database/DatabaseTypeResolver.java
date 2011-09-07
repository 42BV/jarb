package org.jarbframework.violation.resolver.database;


/**
 * Resolves the type of database that we are working with.
 * 
 * @author Jeroen van Schagen
 * @since 13-05-2011
 */
public interface DatabaseTypeResolver {

    /**
     * Resolve the type of database that we are working with.
     * @return database type, if it can be resolved
     */
    DatabaseType resolve();

}
