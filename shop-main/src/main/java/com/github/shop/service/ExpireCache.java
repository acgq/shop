package com.github.shop.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ExpireCache {
    private static long DEFAULT_EXPIRE_TIME = 5000;
    private LoadingCache<String, Integer> cache;
    
    public ExpireCache() {
        createCache(DEFAULT_EXPIRE_TIME);
    }
    
    public ExpireCache(long expireTime) {
        createCache(expireTime);
    }
    
    private void createCache(long time) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(time, TimeUnit.MILLISECONDS)
                .concurrencyLevel(4)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return key.length();
                    }
                });
    }
    
    public void put(String key) {
        cache.put(key, 1);
    }
    
    public boolean isExpire(String key) {
        return cache.getIfPresent(key) == null;
    }
}
