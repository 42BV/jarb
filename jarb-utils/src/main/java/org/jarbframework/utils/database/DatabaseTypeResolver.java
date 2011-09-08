package org.jarbframework.utils.database;

import javax.sql.DataSource;

/**
 * Resolves the database type of a {@link DataSource}.
 *
 * @author Jeroen van Schagen
 * @since Sep 8, 2011
 */
public interface DatabaseTypeResolver {

    /**
     * Resolve the database type of a {@link DataSource}.
     * @param dataSource the data source to resolve from
     * @return resolved database type, never {@code null}
     * @throws UnrecognizedDatabaseException whenever we
     * could not recognize the type of database
     */
    DatabaseType resolve(DataSource dataSource);

}
