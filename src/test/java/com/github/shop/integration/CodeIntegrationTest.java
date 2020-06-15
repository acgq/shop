package com.github.shop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.shop.ShopApplication;
import com.github.shop.exception.StatusResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static com.github.shop.service.CheckInputIsValidServiceImplTest.EMPTY_TEL;
import static com.github.shop.service.CheckInputIsValidServiceImplTest.VALID_TEL;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ActiveProfiles("test")
public class CodeIntegrationTest extends AbstractIntegrationTest {


    @Test
    public void loginAndLogoutTest() throws JsonProcessingException {
        //cookie list
        List<String> sessionCookie;
        //开始获取用户状态，login = false;
        HttpResponse httpResponse = getRequest("/api/status", null);
        StatusResponse statusResponse = objectMapper.readValue(httpResponse.body, StatusResponse.class);
        Assertions.assertFalse(statusResponse.isLogin());
        //登录
        sessionCookie = loginAndGetCookie();
        //登录获取状态
        httpResponse = getRequest("/api/status", sessionCookie);
        statusResponse = objectMapper.readValue(httpResponse.body, StatusResponse.class);
        Assertions.assertTrue(statusResponse.isLogin());
        //登出
        httpResponse = postRequest("/api/logout", "", sessionCookie);
        Assertions.assertEquals(SC_OK, httpResponse.statusCode);
        //检查登录状态
        httpResponse = getRequest("/api/status", sessionCookie);
        statusResponse = objectMapper.readValue(httpResponse.body, StatusResponse.class);
        Assertions.assertFalse(statusResponse.isLogin());

    }


    @Test
    public void returnUnauthorizedWhenNotLogin() {
        HttpResponse httpResponse = getRequest("/api/any", null);
        Assertions.assertEquals(401, httpResponse.statusCode);
    }


    @Test
    public void returnHttpOkWhenParameterValid() throws JsonProcessingException {
        HttpResponse httpResponse = postRequest("/api/code",
                objectMapper.writeValueAsString(VALID_TEL),
                null);
        Assertions.assertEquals(SC_OK, httpResponse.statusCode);
    }

    @Test
    public void returnBadRequestWhenParaMeterInvalid() throws JsonProcessingException {
        HttpResponse httpResponse = postRequest("/api/code",
                objectMapper.writeValueAsString(EMPTY_TEL),
                null);
        Assertions.assertEquals(SC_BAD_REQUEST, httpResponse.statusCode);
    }
}
