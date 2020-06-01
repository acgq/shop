package com.github.shop.service.impl;

import com.github.shop.controller.AuthController;
import com.github.shop.service.InputCheckService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class InputCheckServiceImpl implements InputCheckService {
    private static final Pattern telPattern = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    public boolean verifyTelParameter(AuthController.TelAndCode tel) {
        return Optional.ofNullable(tel)
                .map(telAndCode -> telPattern.matcher(telAndCode.getTel()).matches())
                .orElse(false);
    }
}
