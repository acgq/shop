package com.github.shop.controller;

import com.github.shop.entity.StatusResponse;
import com.github.shop.service.AuthService;
import com.github.shop.service.InputCheckService;
import com.github.shop.service.UserContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;
    private final InputCheckService inputCheckService;

    @Autowired
    public AuthController(AuthService authService, InputCheckService inputCheckService) {
        this.authService = authService;
        this.inputCheckService = inputCheckService;
    }

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
     * 获取用户登录状态
     *
     * @return 用户登录状态
     */
    @GetMapping("/status")
    public StatusResponse getStatus() {
        User user = UserContext.getUser();
        if (user != null) {
            return StatusResponse.loginResponse(user);
        } else {
            return StatusResponse.notLoginResponse();
        }
    }


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
        SecurityUtils.getSubject().login(token);
    }

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
