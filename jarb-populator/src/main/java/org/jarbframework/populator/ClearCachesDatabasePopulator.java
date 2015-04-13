/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.populator;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Database populator that clears the cache.
 *
 * @author Jeroen van Schagen
 * @since Apr 10, 2015
 */
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
