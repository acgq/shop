package com.github.shop.config;

import com.github.shop.service.VerificationService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRealm extends AuthorizingRealm {
    private final VerificationService verificationService;

    @Autowired
    public UserRealm(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String tel = (String) token.getPrincipal();
        String verificationCode = verificationService.getVerificationCode(tel);
        AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(tel, verificationCode, this.getName());
        return authenticationInfo;
    }
}
