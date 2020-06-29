package com.github.shop.config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {
    
    private static final String COOKIE_NAME = "rememberMe"; //  cookie name
    
    private static final int EXPIRY_TIME = 86400; // rememberMe expiry time is 24h
    
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
        
        return shiroFilterFactoryBean;
    }
    
    @Bean
    public SecurityManager securityManager(Realm realm, RedisCacheManager redisCacheManager,
                                           RememberMeManager rememberMeManager) {
        DefaultSecurityManager securityManager = new DefaultWebSecurityManager(realm);
        securityManager.setCacheManager(redisCacheManager);
        securityManager.setRememberMeManager(rememberMeManager);
        securityManager.setSessionManager(new DefaultWebSessionManager());
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }
    
    /**
     * shiro default rememberMe is has security problems, use custom cipher key and set expiry time to 24h
     *
     * @return
     */
    @Bean
    public RememberMeManager rememberMeManager() {
        SimpleCookie cookie = new SimpleCookie(COOKIE_NAME);
        cookie.setMaxAge(EXPIRY_TIME);
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(cookie);
        cookieRememberMeManager.setCipherKey(Base64.decode("3AvVhmFLUs0KTA3KaTHGFg=="));  // RememberMe cookie encryption key default AES algorithm of key length (128, 256, 512)
        return cookieRememberMeManager;
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
