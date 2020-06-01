package com.github.shop.service;

import com.github.shop.controller.AuthController;
import com.github.shop.service.impl.InputCheckServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CheckInputIsValidServiceImplTest {
    public static final AuthController.TelAndCode VALID_TEL = new AuthController.TelAndCode("13000000000", null);
    public static final AuthController.TelAndCode EMPTY_TEL = new AuthController.TelAndCode("", "");


    @Test
    public void testTelIsValid() {
        InputCheckService inputCheckService = new InputCheckServiceImpl();
        Assertions.assertTrue(inputCheckService.verifyTelParameter(VALID_TEL));
    }

    @Test
    public void testTelIsInvalid() {
        InputCheckService inputCheckService = new InputCheckServiceImpl();
        Assertions.assertFalse(inputCheckService.verifyTelParameter(EMPTY_TEL));
        Assertions.assertFalse(inputCheckService.verifyTelParameter(null));
    }

}