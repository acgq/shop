package com.github.shop.config;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

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
    public SecurityManager securityManager(Realm realm) {
        DefaultSecurityManager securityManager = new DefaultWebSecurityManager(realm);
        return securityManager;
    }

}
