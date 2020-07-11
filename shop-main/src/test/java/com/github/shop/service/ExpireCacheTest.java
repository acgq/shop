package com.github.shop.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExpireCacheTest {
    
    @Test
    public void testCache() throws InterruptedException {
        ExpireCache cache = new ExpireCache(200);
        
        cache.put("hello");
        
        Thread.sleep(300);
        Assertions.assertEquals(true, cache.isExpire("hello"));
    }
    
}