package com.github.shop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;

@Service
public class VerificationService {
    private static final int DEFAULT_EXPIRE_TIME_IN_SECONDS = 60;
    @Value("${spring.redis.host}")
    private String redisHost;
    
    @Value("${spring.redis.port}")
    private int redisPort;
    
    Jedis jedis;
    
    public VerificationService() {
    }
    
    @PostConstruct
    private void setup() {
        jedis = new Jedis(redisHost, redisPort);
    }
    
    public String getVerificationCode(String tel) {
        return jedis.get(tel);
    }
    
    public void addCode(String tel, String codeSent) {
        jedis.setex(tel, DEFAULT_EXPIRE_TIME_IN_SECONDS, codeSent);
    }
}
