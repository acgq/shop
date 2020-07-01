package com.github.shop.service;

import com.github.shop.service.impl.InputCheckServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.shop.TestUtils.EMPTY_TEL;
import static com.github.shop.TestUtils.VALID_TEL;

public class CheckInputIsValidServiceImplTest {
    
    
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