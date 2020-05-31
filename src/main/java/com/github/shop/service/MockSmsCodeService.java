package com.github.shop.service;

import org.springframework.stereotype.Service;

@Service
public class MockSmsCodeService implements SmsCodeService {
    @Override
    public String sendVerificationCode(String tel) {
        return "000000";
    }
}
