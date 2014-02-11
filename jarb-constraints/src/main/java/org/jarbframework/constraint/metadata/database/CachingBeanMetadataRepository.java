/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata.database;

import java.util.HashMap;
import java.util.Map;

import org.jarbframework.utils.bean.PropertyReference;

/**
 * Caching bean metadata repository.
 *
 * @author Jeroen van Schagen
 * @since Feb 11, 2014
 */
public class CachingBeanMetadataRepository implements BeanMetadataRepository {
    
    private final Map<PropertyReference, ColumnMetadata> caches = new HashMap<>();

    private final BeanMetadataRepository delegate;

    public CachingBeanMetadataRepository(BeanMetadataRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public ColumnMetadata getColumnMetadata(PropertyReference propertyReference) {
        ColumnMetadata columnMetadata = caches.get(propertyReference);
        if (columnMetadata == null) {
            columnMetadata = delegate.getColumnMetadata(propertyReference);
            caches.put(propertyReference, columnMetadata);
        }
        return columnMetadata;
    }
    
    @Override
    public boolean isEmbeddable(Class<?> propertyType) {
        return delegate.isEmbeddable(propertyType);
    }
    
    public void evictAll() {
        caches.clear();
    }

}
