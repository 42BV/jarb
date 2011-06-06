package org.jarb.constraint.database.named;

import java.util.List;

/**
 * Provides access to named constraint metadata.
 * 
 * @author Jeroen van Schagen
 * @since 6-6-2011
 */
public interface NamedConstraintMetadataRepository {

    /**
     * Retrieve all named constraints inside the database.
     * @return list of all named constraints
     */
    List<NamedConstraintMetadata> all();

    /**
     * Retrieve a specific named constraint.
     * @param name constraint name
     * @return metadata of the constraint
     */
    NamedConstraintMetadata named(String name);

}
