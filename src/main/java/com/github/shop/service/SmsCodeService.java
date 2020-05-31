package com.github.shop.service;

import org.springframework.stereotype.Service;

@Service
public interface SmsCodeService {

    public String sendVerificationCode(String tel);
}
