package com.github.shop.controller;

import com.github.shop.entity.StatusResponse;
import com.github.shop.exception.UnauthenticatedException;
import com.github.shop.generate.User;
import com.github.shop.service.AuthService;
import com.github.shop.service.InputCheckService;
import com.github.shop.service.UserContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthService authService;
    private final InputCheckService inputCheckService;
    
    @Autowired
    public AuthController(AuthService authService, InputCheckService inputCheckService) {
        this.authService = authService;
        this.inputCheckService = inputCheckService;
    }
    
    
    /**
     * @api {post} /code 请求验证码
     * @apiName GetCode
     * @apiGroup 登录与鉴权
     *
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/json
     *
     * @apiParam {String} tel 手机号码
     * @apiParamExample {json} Request-Example:
     *          {
     *              "tel": "13812345678",
     *          }
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     * @apiError 400 Bad Request 若用户的请求包含错误
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 400 Bad Request
     *     {
     *       "message": "Bad Request"
     *     }
     */
    /**
     * 获取验证码
     *
     * @param response   数据不合法，返回400
     * @param telAndCode 用户名和验证码
     */
    @PostMapping("/code")
    public void getVerificationCode(@RequestBody TelAndCode telAndCode, HttpServletResponse response) {
        if (inputCheckService.verifyTelParameter(telAndCode)) {
            String tel = telAndCode.getTel();
            authService.sendCodeAndStore(tel);
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }
    
    /**
     * @api {get} /status 获取登录状态
     * @apiName Status
     * @apiGroup 登录与鉴权
     *
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/json
     *
     * @apiSuccess {User} user 用户信息
     * @apiSuccess {Boolean} login 登录状态
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "login": true,
     *       "user": {
     *           "id": 123,
     *           "name": "张三",
     *           "tel": "13812345678",
     *           "avatarUrl": "https://url",
     *           "address": "北京市 西城区 100号",
     *       }
     *     }
     *
     * @apiError 401 Unauthorized 若用户未登录
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    /**
     * 获取用户登录状态
     *
     * @return 用户登录状态
     */
    @GetMapping("/status")
    public StatusResponse getStatus() {
        System.out.println("轮到我了");
        User user = UserContext.getUser();
        if (user != null) {
            return StatusResponse.loginResponse(user);
        } else {
            return StatusResponse.notLoginResponse();
        }
    }
    
    
    /**
     * @api {post} /login 登录
     * @apiName Login
     * @apiGroup 登录与鉴权
     *
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/json
     *
     * @apiParam {String} tel 手机号码
     * @apiParam {String} code 验证码
     * @apiParamExample {json} Request-Example:
     *          {
     *              "tel": "13812345678",
     *              "code": "000000"
     *          }
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     * @apiError 400 Bad Request 若用户的请求包含错误
     * @apiError 403 Forbidden 若用户的验证码错误
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 400 Bad Request
     *     {
     *       "message": "Bad Request"
     *     }
     */
    /**
     * 使用手机号和验证码登录
     *
     * @param telAndCode 手机号和验证码
     */
    @PostMapping("/login")
    public void login(@RequestBody TelAndCode telAndCode) {
        UsernamePasswordToken token = new UsernamePasswordToken(
                telAndCode.getTel(),
                telAndCode.getCode());
        token.setRememberMe(true);
        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            throw new UnauthenticatedException("验证码错误");
        }
    }
    
    
    /**
     * @api {post} /logout 登出
     * @apiName Logout
     * @apiGroup 登录与鉴权
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/json
     * @apiSuccessExample Success-Response:
     * HTTP/1.1 200 OK
     * @apiError 401 Unauthorized 若用户未登录
     * @apiErrorExample Error-Response:
     * HTTP/1.1 400 Bad Request
     * {
     * "message": "Bad Request"
     * }
     */
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public void logout() {
        SecurityUtils.getSubject().logout();
    }
    
    public static class TelAndCode {
        private String tel;
        private String code;
        
        public TelAndCode(String tel, String code) {
            this.tel = tel;
            this.code = code;
        }
        
        
        public String getTel() {
            return tel;
        }
        
        public void setTel(String tel) {
            this.tel = tel;
        }
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
    }
}
