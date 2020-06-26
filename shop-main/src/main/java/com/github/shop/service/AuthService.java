package com.github.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private VerificationService verificationService;
    private SmsCodeService smsCodeService;
    private UserService userService;

    @Autowired
    public AuthService(VerificationService verificationService, SmsCodeService smsCodeService, UserService userService) {
        this.verificationService = verificationService;
        this.smsCodeService = smsCodeService;
        this.userService = userService;
    }

    public void sendCodeAndStore(String tel) {
        userService.createUserIfNotExist(tel);
        String codeSent = smsCodeService.sendVerificationCode(tel);
        verificationService.addCode(tel, codeSent);
    }
}
