package com.github.shop.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.shop.ShopApplication;
import com.github.shop.entity.StatusResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static com.github.shop.service.CheckInputIsValidServiceImplTest.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yml")
public class CodeIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    Environment environment;
    private List<String> sessionCookie;

    private String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

    @Test
    public void loginAndLogoutTest() throws JsonProcessingException {
        //开始获取用户状态，login = false;
        HttpResponse httpResponse = getRequest("/api/status", null);
        StatusResponse statusResponse = objectMapper.readValue(httpResponse.body, StatusResponse.class);
        Assertions.assertFalse(statusResponse.isLogin());
        //发送验证码
        String loginJson = objectMapper.writeValueAsString(VALID_PARAMETER);
        postRequest("/api/code", loginJson, null);
        //登录
        httpResponse = postRequest("/api/login", loginJson, null);
        Assertions.assertEquals(httpResponse.statusCode, OK.value());
        String sessionId = getSessionCookie(httpResponse.headers.get("Set-Cookie").stream()
                .filter(header -> header.contains("SESSION"))
                .findFirst().get());
        sessionCookie = Lists.list(sessionId);
        //登录获取状态
        httpResponse = getRequest("/api/status", sessionCookie);
        statusResponse = objectMapper.readValue(httpResponse.body, StatusResponse.class);
        Assertions.assertTrue(statusResponse.isLogin());
        //登出
        httpResponse = postRequest("/api/logout", "", Lists.list(sessionId));
        Assertions.assertEquals(OK.value(), httpResponse.statusCode);
        //检查登录状态
        httpResponse = getRequest("/api/status", Lists.list(sessionId));
        statusResponse = objectMapper.readValue(httpResponse.body, StatusResponse.class);
        Assertions.assertFalse(statusResponse.isLogin());

    }

    //JSESSIONID=74AD6B4D1BB35C2F9C24E40EDCC71C6E; Path=/; HttpOnly  -> JSESSIONID=74AD6B4D1BB35C2F9C24E40EDCC71C6E
    public String getSessionCookie(String cookie) {
        int endPosition = cookie.indexOf(';');
        return cookie.substring(0, endPosition);
    }

    public HttpResponse getRequest(String apiName, List<String> cookies) {
        HttpRequest request = HttpRequest.get(getUrl(apiName));
        if (cookies != null) {
            cookies.forEach(cookie -> request.header("Cookie", cookie));
        }
        return new HttpResponse(request.code(), request.body(), request.headers());
    }

    public HttpResponse postRequest(String apiName, String body, List<String> cookies) {
        HttpRequest request = HttpRequest.get(getUrl(apiName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        if (cookies != null) {
            cookies.forEach(cookie -> request.header("Cookie", cookie));
        }
        HttpRequest send = request.send(body);
        return new HttpResponse(send.code(), send.body(), send.headers());
    }

    private static class HttpResponse {
        private int statusCode;
        private String body;
        private Map<String, List<String>> headers;

        HttpResponse(int statusCode, String body, Map<String, List<String>> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }

        HttpResponse() {
        }
    }


    @Test
    public void returnHttpOkWhenParameterValid() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(VALID_TEL))
                .code();
        Assertions.assertEquals(responseCode, OK.value());
    }

    @Test
    public void returnBadRequestWhenParaMeterInvalid() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(EMPTY_TEL))
                .code();
        Assertions.assertEquals(BAD_REQUEST.value(), responseCode);
    }
}
