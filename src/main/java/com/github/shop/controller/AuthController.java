package com.github.shop.controller;

import com.github.shop.service.AuthService;
import com.github.shop.service.InputCheckService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * User get verification code.
     *
     * @param response
     * @param telAndCode tel number
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
     * @param telAndCode
     */
    @PostMapping("/login")
    public void login(@RequestBody TelAndCode telAndCode) {
        UsernamePasswordToken token = new UsernamePasswordToken(
                telAndCode.getTel(),
                telAndCode.getCode());
        token.setRememberMe(true);
        SecurityUtils.getSubject().login(token);
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
