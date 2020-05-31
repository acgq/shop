package com.github.shop.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {
    Map<String, String> cachedTelVerificationCode = new ConcurrentHashMap<>();

    public String getVerificationCode(String tel) {
        return cachedTelVerificationCode.get(tel);
    }

    public void addCode(String tel, String codeSent) {
        cachedTelVerificationCode.put(tel, codeSent);
    }
}
