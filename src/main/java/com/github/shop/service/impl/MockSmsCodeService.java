package com.github.shop.service.impl;

import com.github.shop.service.SmsCodeService;
import org.springframework.stereotype.Service;

@Service
public class MockSmsCodeService implements SmsCodeService {
    @Override
    public String sendVerificationCode(String tel) {
        return "000000";
    }
}
