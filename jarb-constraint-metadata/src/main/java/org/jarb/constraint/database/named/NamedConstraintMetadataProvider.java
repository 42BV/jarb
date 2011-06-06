package org.jarb.constraint.database.named;

import java.util.Set;

/**
 * Capable of retrieving all named constraint metadata.
 * 
 * @author Jeroen van Schagen
 * @since 30-05-2011
 */
public interface NamedConstraintMetadataProvider {

    /**
     * Retrieve the metadata of all named constraints inside our database.
     * @return all named constraint metadata
     */
    Set<NamedConstraintMetadata> all();

}
