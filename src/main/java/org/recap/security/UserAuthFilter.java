package org.recap.security;

import org.apache.shiro.web.filter.authc.AuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by dharmendrag on 2/12/16.
 */
public class UserAuthFilter extends AuthenticationFilter {

    public UserAuthFilter(){
        super();
        System.out.println("Filter call");
    }
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return false;
    }
}
