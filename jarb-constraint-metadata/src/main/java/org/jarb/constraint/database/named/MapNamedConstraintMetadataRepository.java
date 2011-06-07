package org.jarb.constraint.database.named;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Retrieves constraint metadata from a map.
 * 
 * @author Jeroen van Schagen
 * @since 7-6-2011
 */
public class MapNamedConstraintMetadataRepository implements NamedConstraintMetadataRepository {
    private Map<String, NamedConstraintMetadata> constraintMetadataMap = new LinkedHashMap<String, NamedConstraintMetadata>();

    public MapNamedConstraintMetadataRepository store(NamedConstraintMetadata constraintMetadata) {
        Assert.notNull(constraintMetadata, "Cannot store null pointer named constraint metadata");
        constraintMetadataMap.put(constraintMetadata.getName(), constraintMetadata);
        return this;
    }

    public void removeAll() {
        constraintMetadataMap.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamedConstraintMetadata named(String name) {
        NamedConstraintMetadata constraintMetadata = null;
        if (constraintMetadataMap.containsKey(name)) {
            constraintMetadata = constraintMetadataMap.get(name);
        }
        return constraintMetadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NamedConstraintMetadata> all() {
        return new ArrayList<NamedConstraintMetadata>(constraintMetadataMap.values());
    }

}
