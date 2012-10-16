package org.jarbframework.constraint.metadata.database;

import org.jarbframework.utils.Asserts;
import org.jarbframework.utils.orm.ColumnReference;

/**
 * Repository that loads all constraint meta-data in memory, minimizing database
 * connectivity. With a caching constraint repository, the retrieval of constraint
 * metadata should be significantly faster.
 * 
 * @author Jeroen van Schagen
 * @since 24-05-2011
 */
public class CachingColumnMetadataRepository implements ColumnMetadataRepository {

    /** Maintains all constraint meta-data in memory **/
    private final MapColumnMetadataRepository columnMetadataCache = new MapColumnMetadataRepository();

    /** Capable of filling our cache with constraint meta-data **/
    private final ColumnMetadataRepository columnMetadataRepository;

    /**
     * Construct a new {@link CachingColumnMetadataRepository}.
     * @param columnMetadataRepository provides column meta-data for our cache
     */
    public CachingColumnMetadataRepository(ColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = Asserts.notNull(columnMetadataRepository, "Delegate column metadata repository cannot be null.");
    }

    @Override
    public ColumnMetadata getColumnMetadata(ColumnReference columnReference) {
        ColumnMetadata columnMetadata = columnMetadataCache.getColumnMetadata(columnReference);
        if (columnMetadata == null) {
            columnMetadata = lookupAndCacheColumnMetadata(columnReference);
        }
        return columnMetadata;
    }

    private ColumnMetadata lookupAndCacheColumnMetadata(ColumnReference columnReference) {
        ColumnMetadata columnMetadata = columnMetadataRepository.getColumnMetadata(columnReference);
        if (columnMetadata != null) {
            columnMetadataCache.add(columnMetadata);
        }
        return columnMetadata;
    }

    /**
     * Remove all cached metdata. Clearing the cache might be desirable
     * whenever the table structure has changed during runtime.
     */
    public void clearCache() {
        columnMetadataCache.removeAll();
    }

}
