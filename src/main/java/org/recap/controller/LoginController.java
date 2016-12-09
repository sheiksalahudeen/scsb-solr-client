package org.recap.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.recap.model.userManagement.LoginValidator;
import org.recap.model.userManagement.UserForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


/**
 * Created by dharmendrag on 25/11/16.
 */
@Controller
public class LoginController {

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    private LoginValidator loginValidator=new LoginValidator();

    @RequestMapping(value="/",method= RequestMethod.GET)
    public String loginScreen(HttpServletRequest request, Model model,@ModelAttribute UserForm userForm) {
        return "login";
    }


    @RequestMapping(value="/",method=RequestMethod.POST)
    public String createSession(@Valid @ModelAttribute UserForm userForm, HttpServletRequest request, Model model, BindingResult error){
        loginValidator.validate(userForm,error);
        if(userForm==null){
            return "login";
        }
        try
        {
            if(error.hasErrors())
            {
                return loginScreen(request,model,userForm);
            }
            UsernamePasswordToken token = new UsernamePasswordToken(userForm.getUsername(),userForm.getPassword(),true);
            Subject subject=SecurityUtils.getSubject();
            subject.login(token);
            boolean auth=subject.isAuthenticated();
            if(!auth)
            {
                return "login";
            }

        }
        catch(AuthenticationException e)
        {
            error.rejectValue("wrongCredentials","error.invalid.credentials","Invalid Credentials");
            return "login";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return "login";
        }

            return "redirect:/search";

    }

    @RequestMapping("/logout")
    public String logoutUser(){
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }

}
