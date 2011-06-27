package org.jarb.populator.excel.mapping.exporter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Resolves the row identifier of a specific entity. Note that the
 * row identifier is only used inside our Excel workbook, and has
 * no relation to the official entity identifier, as used in the
 * database.
 * 
 * @author Jeroen van Schagen
 * @since 24-05-2011
 */
public class EntityRowIdResolver {
    private Map<Object, Object> identityCache = new HashMap<Object, Object>();
    private long counter = 1;
    
    /**
     * Resolve the row identifier of an entity.
     * @param entity the entity to identify
     * @return row identifier for entity
     */
    public Object resolveRowId(Object entity) {
        Assert.notNull(entity, "Cannot resolve the row identifier of a null entity");
        Object identifier = null;
        if(identityCache.containsKey(entity)) {
            identifier = identityCache.get(entity);
        } else {
            identifier = counter++;
            identityCache.put(entity, identifier);
        }
        return identifier;
    }
    
}
