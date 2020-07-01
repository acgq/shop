package com.github.shop.integration;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.kevinsawicki.http.HttpRequest;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.shop.TestUtils.VALID_PARAMETER;
import static com.github.shop.TestUtils.VALID_PARAMETER_USER2;

public class AbstractIntegrationTest {
    protected static ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Autowired
    Environment environment;
    
    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String datasourcePassword;
    
    @BeforeEach
    public void initDatabase() {
        ClassicConfiguration configuration = new ClassicConfiguration();
        configuration.setDataSource(databaseUrl, databaseUsername, datasourcePassword);
        Flyway flyway = new Flyway(configuration);
        flyway.clean();
        flyway.migrate();
    }
    
    //login with login json
    //user id is 1 , which is created in V1__CreateUser.sql
    protected List<String> loginAndGetCookie() {
        //发送验证码
        postRequest("/api/v1/code", VALID_PARAMETER, null);
        //登录
        HttpResponse httpResponse = postRequest("/api/v1/login", VALID_PARAMETER, null);
        return Optional.ofNullable(httpResponse.headers.get("Set-Cookie"))
                .map(headers -> headers.stream()
                        .filter(header -> header.contains("JSESSIONID"))
                        .map(s -> getSessionCookie(s))
                        .collect(Collectors.toList()))
                .orElse(null);
    }
    
    protected List<String> loginAndGetCookieWithUser2() {
        //发送验证码
        postRequest("/api/v1/code", VALID_PARAMETER_USER2, null);
        //登录
        HttpResponse httpResponse = postRequest("/api/v1/login", VALID_PARAMETER_USER2, null);
        return httpResponse.headers.get("Set-Cookie").stream()
                .filter(header -> !header.contains("deleteMe"))
                .map(s -> getSessionCookie(s))
                .collect(Collectors.toList());
    }
    
    public String toJson(Object data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }
    
    private String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }
    
    //JSESSIONID=74AD6B4D1BB35C2F9C24E40EDCC71C6E; Path=/; HttpOnly  -> JSESSIONID=74AD6B4D1BB35C2F9C24E40EDCC71C6E
    public String getSessionCookie(String cookie) {
        int endPosition = cookie.indexOf(';');
        return cookie.substring(0, endPosition);
    }
    
    public HttpResponse getRequest(String apiName, List<String> cookies) {
        if (!apiName.startsWith("/")) {
            apiName = "/" + apiName;
        }
        HttpRequest request = HttpRequest.get(getUrl(apiName));
        if (cookies != null) {
            cookies.forEach(cookie -> request.header("Cookie", cookie));
        }
        return new HttpResponse(request.code(), request.body(), request.headers());
    }
    
    public HttpResponse postRequest(String apiName, Object body, List<String> cookies) {
        return generalPost(apiName, body, cookies, HttpRequest.METHOD_POST);
    }
    
    
    public HttpResponse updateRequest(String apiName, Object body, List<String> cookies) throws JsonProcessingException {
        HttpRequest request = new HttpRequest(getUrl(apiName), HttpRequest.METHOD_POST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        if (cookies != null) {
            cookies.forEach(cookie -> request.header("Cookie", cookie));
        }
        request.header("X-HTTP-Method-Override", "PATCH");
        HttpRequest send = request.send(objectMapper.writeValueAsString(body));
        return new HttpResponse(send.code(), send.body(), send.headers());
    }
    
    public HttpResponse deleteRequest(String apiName, Object body, List<String> cookie) {
        return generalPost(apiName, body, cookie, HttpRequest.METHOD_DELETE);
        
    }
    
    private HttpResponse generalPost(String apiName, Object body, List<String> cookies, String method) {
        if (!apiName.startsWith("/")) {
            apiName = "/" + apiName;
        }
        HttpRequest request = new HttpRequest(getUrl(apiName), method)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        if (cookies != null) {
            cookies.forEach(cookie -> request.header("Cookie", cookie));
        }
        String jsonBody = null;
        try {
            jsonBody = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HttpRequest send = request.send(jsonBody);
        return new HttpResponse(send.code(), send.body(), send.headers());
    }
    
    
    protected static class HttpResponse {
        protected int statusCode;
        protected String body;
        protected Map<String, List<String>> headers;
        
        protected HttpResponse(int statusCode, String body, Map<String, List<String>> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }
        
        public <T> T asJson(TypeReference<T> typeReference) {
            T res = null;
            try {
                res = objectMapper.readValue(body, typeReference);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return res;
        }
    }
}
