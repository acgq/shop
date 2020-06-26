package com.github.shop.service;

import com.github.shop.controller.AuthController;

public interface InputCheckService {

    boolean verifyTelParameter(AuthController.TelAndCode tel);
}
