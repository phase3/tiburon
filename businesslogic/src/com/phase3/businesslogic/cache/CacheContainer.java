package com.phase3.businesslogic.cache;

import java.util.concurrent.*;

public class CacheContainer {
    ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    //TODO Age
    public CacheContainer() {
    }
    public void cache(String fullPath, String value) {
        cache.put(fullPath,value);
    }
    public String getCached(String fullPath) {
        return cache.get(fullPath);
    }
    public void invalidate(String path) {
        cache.remove(path);
    }

}
