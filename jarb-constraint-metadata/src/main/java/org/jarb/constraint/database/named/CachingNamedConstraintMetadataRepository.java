package org.jarb.constraint.database.named;

import java.util.List;

import org.springframework.util.Assert;

public class CachingNamedConstraintMetadataRepository implements NamedConstraintMetadataRepository {
    private final MapNamedConstraintMetadataRepository constraintMetadataCache = new MapNamedConstraintMetadataRepository();
    /** Capable of filling our cache with constraint metadata **/
    private final NamedConstraintMetadataProvider constraintMetadataProvider;
    /** Describes whether our cache has been filled yet, or not **/
    private boolean metadataCached = false;

    /**
     * Construct a new {@link CachingNamedConstraintMetadataRepository}.
     * @param constraintMetadataProvider provides metadata for our cache
     */
    public CachingNamedConstraintMetadataRepository(NamedConstraintMetadataProvider constraintMetadataProvider) {
        Assert.notNull(constraintMetadataProvider, "Property 'constraint metadata provider' cannot be null");
        this.constraintMetadataProvider = constraintMetadataProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamedConstraintMetadata named(String name) {
        checkCache();
        return constraintMetadataCache.named(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NamedConstraintMetadata> all() {
        checkCache();
        return constraintMetadataCache.all();
    }

    /**
     * Cache the column metadata, whenever needed.
     */
    private synchronized void checkCache() {
        if (!metadataCached) {
            fillCache();
        }
    }

    /**
     * Retrieve all column metadata and store it inside our cache.
     */
    private void fillCache() {
        for (NamedConstraintMetadata constraintMetadata : constraintMetadataProvider.all()) {
            constraintMetadataCache.store(constraintMetadata);
        }
        metadataCached = true;
    }

    /**
     * Remove all cached metdata. Clearing the cache might be desirable
     * whenever the table structure has changed during runtime.
     */
    public synchronized void clearCache() {
        constraintMetadataCache.removeAll();
        metadataCached = false;
    }

}
