package com.github.shop.service;

import org.springframework.stereotype.Service;

@Service
public interface SmsCodeService {

    String sendVerificationCode(String tel);
}
