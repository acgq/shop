package com.github.shop.config;

import com.github.shop.service.UserContext;
import com.github.shop.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 使用拦截器来处理UserContext
 */

@Component
public class ContextInterceptor implements HandlerInterceptor {
    UserService userService;

    @Autowired
    public ContextInterceptor(UserService userService) {
        this.userService = userService;
    }

    //处理请前根据用户tel获得用户信息存入UserContext中
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tel = (String) SecurityUtils.getSubject().getPrincipal();
        if (tel != null) {
            userService.getUserByTel(tel).ifPresent(UserContext::setUser);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    //请求完成后从Context中清除用户信息
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clearUser();
    }
}
