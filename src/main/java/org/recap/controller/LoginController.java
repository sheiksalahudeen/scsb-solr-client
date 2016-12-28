package org.recap.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.recap.model.userManagement.LoginValidator;
import org.recap.model.userManagement.UserForm;
import org.recap.security.UserManagement;
import org.recap.security.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;


/**
 * Created by dharmendrag on 25/11/16.
 */
@Controller
public class LoginController {

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    private LoginValidator loginValidator=new LoginValidator();

    @Autowired
    private UserService userService;

    @RequestMapping(value="/",method= RequestMethod.GET)
    public String loginScreen(HttpServletRequest request, Model model,@ModelAttribute UserForm userForm) {
        logger.info("Login Screen called");
        return "login";
    }


    @RequestMapping(value="/",method=RequestMethod.POST)
    public String createSession(@Valid @ModelAttribute UserForm userForm, HttpServletRequest request, Model model, BindingResult error){
        loginValidator.validate(userForm,error);
        final String loginScreen="login";
        Map<Integer,String> permissionMap=null;
        if(userForm==null){
            return loginScreen;
        }
        try
        {
            if(error.hasErrors())
            {
                logger.debug("Login Screen validation failed");
                return loginScreen(request,model,userForm);
            }
            UsernamePasswordToken token = new UsernamePasswordToken(userForm.getUsername()+ UserManagement.TOKEN_SPLITER.getValue()+userForm.getInstitution(),userForm.getPassword(),false);
            Subject subject=SecurityUtils.getSubject();
            subject.login(token);
            if(!subject.isAuthenticated())
            {
                throw new AuthenticationException("Subject Authtentication Filed");
            }
            permissionMap=userService.getPermissions();
            Session session=subject.getSession(true);
            session.setAttribute("userName",userForm.getUsername());
            session.setAttribute(UserManagement.USER_INSTITUTION,userForm.getInstitution());
            session.setAttribute("userForm",userForm);
            session.setAttribute(UserManagement.USER_ID,subject.getPrincipal());
            session.setAttribute(UserManagement.permissionsMap, Collections.unmodifiableMap(permissionMap));

        }
        catch(AuthenticationException e)
        {
            logger.debug("Authentication exception");
            logger.error("Exception in authentication : "+e.getMessage());
            error.rejectValue("wrongCredentials","error.invalid.credentials","Invalid Credentials");
            return loginScreen;
        }
        catch(Exception e)
        {
            logger.error("Exception occured in authentication : "+e.getLocalizedMessage());
            return loginScreen;
        }

            return "redirect:/search";

    }

    @RequestMapping("/logout")
    public String logoutUser(){
        logger.info("Subject Logged out");
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }


}
