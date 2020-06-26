package com.github.shop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shop.entity.Response;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ShiroLoginFilter extends FormAuthenticationFilter {
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String message = new ObjectMapper().writeValueAsString(Response.ofMessage("Unauthorized"));
        httpServletResponse.getWriter().write(message);
        return false;
    }
}
