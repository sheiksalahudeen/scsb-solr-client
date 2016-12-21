package org.recap.security.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.recap.model.userManagement.UserForm;
import org.recap.security.AuthenticationService;
import org.recap.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by dharmendrag on 29/11/16.
 */
@Component
public class SimpleAuthorizationRealm extends AuthorizingRealm{



    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AuthorizationService authorizationService;

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }



    public SimpleAuthorizationRealm(){
        setName("simpleAuthRealm");
        setCredentialsMatcher(new SimpleCredentialsMatcher());
        setCachingEnabled(true);
    }



    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Integer loginId=(Integer)principals.fromRealm(getName()).iterator().next();
        SimpleAuthorizationInfo authorizationInfo=new SimpleAuthorizationInfo();
        return authorizationService.doAuthorizationInfo(authorizationInfo,loginId);

    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        UsernamePasswordToken token=(UsernamePasswordToken)authToken;
        UserForm userForm=null;
        try {
            userForm=authenticationService.doAuthentication(token);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        if(userForm!=null && userForm.isPasswordMatcher()){
            return new SimpleAuthenticationInfo(userForm.getUserId(),token.getPassword(),getName());
        }
        return null;
    }



}
