/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.populate;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        for (String cacheName : cacheManager.getCacheNames()) {
            cacheManager.getCache(cacheName).clear();
        }
    }

}
