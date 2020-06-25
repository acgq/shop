package com.github.shop.config;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    
    @Value("${spring.redis.host}")
    private String redisHost;
    
    @Value("${spring.redis.port}")
    private String redisPort;
    
    public ShiroConfig() {
    }
    
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        
        LinkedHashMap<String, String> patternMap = new LinkedHashMap<>();
        patternMap.put("/api/code", "anon");
        patternMap.put("/api/login", "anon");
        patternMap.put("/api/register", "anon");
        patternMap.put("/api/status", "anon");
        patternMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(patternMap);
        
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", new ShiroLoginFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        return shiroFilterFactoryBean;
    }
    
    @Bean
    public SecurityManager securityManager(Realm realm, RedisCacheManager redisCacheManager) {
        DefaultSecurityManager securityManager = new DefaultWebSecurityManager(realm);
        securityManager.setCacheManager(redisCacheManager);
        return securityManager;
    }
    
    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(redisHost + ":" + redisPort);
        RedisCacheManager cacheManager = new RedisCacheManager();
        cacheManager.setRedisManager(redisManager);
        return cacheManager;
    }
    
}
