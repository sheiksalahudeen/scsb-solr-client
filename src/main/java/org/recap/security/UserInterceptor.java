package org.recap.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by dharmendrag on 29/11/16.
 */
@Component
public class UserInterceptor extends HandlerInterceptorAdapter{

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        // Add the current user into the request
        /*User currentUser = userService.getCurrentUser();
        if( currentUser != null ) {
            httpServletRequest.setAttribute( "currentUser", currentUser );
        }*/
        System.out.println("***Interceptor***");
    }
}
