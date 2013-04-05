package org.jarbframework.constraint.metadata.database;

import java.util.HashMap;
import java.util.Map;

import org.jarbframework.utils.orm.ColumnReference;

/**
 * Map based column metadata repository.
 * 
 * @author Jeroen van Schagen
 * @since 30-05-2011
 */
public class MapColumnMetadataRepository implements ColumnMetadataRepository {

    /** Map containing all column metadata. **/
    private final Map<ColumnReference, ColumnMetadata> columnMetadataMap;

    /**
     * Construct a new {@link MapColumnMetadataRepository}.
     */
    public MapColumnMetadataRepository() {
        columnMetadataMap = new HashMap<ColumnReference, ColumnMetadata>();
    }

    /**
     * Store a column constraint inside this repository.
     * @param columnMetadata column constraint that should be added
     * @return the same repository instance, for chaining
     */
    public MapColumnMetadataRepository addColumnMetadata(ColumnMetadata columnMetadata) {
        columnMetadataMap.put(columnMetadata.getColumnReference(), columnMetadata);
        return this;
    }

    /**
     * Remove all stored constraints from this repository.
     * @return the same repository instance, for chaining
     */
    public MapColumnMetadataRepository removeAll() {
        columnMetadataMap.clear();
        return this;
    }

    @Override
    public ColumnMetadata getColumnMetadata(ColumnReference columnReference) {
        return columnMetadataMap.get(columnReference);
    }
    
}
