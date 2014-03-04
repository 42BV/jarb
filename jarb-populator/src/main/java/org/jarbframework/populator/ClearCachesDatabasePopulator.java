/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.populator;

import org.jarbframework.populator.DatabasePopulator;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class ClearCachesDatabasePopulator implements DatabasePopulator {
    
    private final CacheManager cacheManager;
    
    public ClearCachesDatabasePopulator(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void populate() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            cache.clear();
        }
    }

}
