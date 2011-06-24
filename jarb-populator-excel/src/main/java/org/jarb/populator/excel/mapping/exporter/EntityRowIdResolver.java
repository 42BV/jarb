package org.jarb.populator.excel.mapping.exporter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

public class EntityRowIdResolver {
    private Map<Object, Object> identityCache = new HashMap<Object, Object>();
    private long counter = 1;
    
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
