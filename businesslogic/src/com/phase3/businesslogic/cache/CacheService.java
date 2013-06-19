package com.phase3.businesslogic.cache;

public class CacheService {
    //todo abstract out impl
    // Singleton manager to aid in unit testing
    private static CacheContainer cacheContainer = new CacheContainer();

    public static CacheContainer getCache() {
        return cacheContainer;
    }

}
