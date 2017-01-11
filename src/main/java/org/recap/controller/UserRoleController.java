package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.RoleEntity;
import org.recap.model.jpa.UsersEntity;
import org.recap.model.userManagement.UserRoleForm;
import org.recap.security.UserManagement;
import org.recap.service.userManagement.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by dharmendrag on 23/12/16.
 */
@Controller
public class UserRoleController {

    Logger logger= LoggerFactory.getLogger(UserRoleController.class);

    @Autowired
    private UserRoleService userRoleService;

    @RequestMapping(value="/userRoles")
    public String showUserRoles(Model model)
    {
        logger.info("Users Tab Clicked");
        UserRoleForm userRoleForm=new UserRoleForm();
            List<Object> roles=userRoleService.getRoles(UserManagement.SUPER_ADMIN.getIntegerValues());
            List<Object> institutions=userRoleService.getInstitutions(false,1);
            userRoleForm.setRoles(roles);
            userRoleForm.setInstitutions(institutions);
            userRoleForm.setAllowCreateEdit(true);
            model.addAttribute("userRoleForm",userRoleForm);
            model.addAttribute(RecapConstants.TEMPLATE,RecapConstants.USER_ROLES);
            return "searchRecords";
    }

    @ResponseBody
    @RequestMapping(value="/userRoles",method = RequestMethod.POST,params="action=searchUsers")
    public ModelAndView searchUserRole(@Valid @ModelAttribute("userForm")UserRoleForm userRoleForm, BindingResult results, Model model)
    {
        logger.info("Users - Search button Clicked");
        priorSearch(userRoleForm);
        model.addAttribute(RecapConstants.TEMPLATE,RecapConstants.USER_ROLES);
        return new ModelAndView("userRolesSearch","userRoleForm",userRoleForm);
    }

    @ResponseBody
    @RequestMapping(value="/userRoles",method = RequestMethod.POST,params="action=first")
    public ModelAndView searchFirstPage(@ModelAttribute("userForm")UserRoleForm userRoleForm, BindingResult results,Model model)
    {
        logger.info("Users - Search First Page button Clicked");
        userRoleForm.resetPageNumber();
        priorSearch(userRoleForm);
        model.addAttribute(RecapConstants.TEMPLATE,RecapConstants.USER_ROLES);
        return new ModelAndView("userRolesSearch","userRoleForm",userRoleForm);
    }

    @ResponseBody
    @RequestMapping(value="/userRoles",method = RequestMethod.POST,params="action=next")
    public ModelAndView searchNextPage(@ModelAttribute("userForm")UserRoleForm userRoleForm, BindingResult results,Model model)
    {
        logger.info("Users - Search Next Page button Clicked");
        priorSearch(userRoleForm);
        model.addAttribute(RecapConstants.TEMPLATE,RecapConstants.USER_ROLES);
        return new ModelAndView("userRolesSearch","userRoleForm",userRoleForm);
    }

    @ResponseBody
    @RequestMapping(value="/userRoles",method = RequestMethod.POST,params="action=previous")
    public ModelAndView searchPreviousPage(@ModelAttribute("userForm")UserRoleForm userRoleForm, BindingResult results,Model model)
    {
        logger.info("Users - Search Previous Page button Clicked");
        priorSearch(userRoleForm);
        model.addAttribute(RecapConstants.TEMPLATE,RecapConstants.USER_ROLES);
        return new ModelAndView("userRolesSearch","userRoleForm",userRoleForm);
    }

    @ResponseBody
    @RequestMapping(value="/userRoles",method = RequestMethod.POST,params="action=last")
    public ModelAndView searchLastPage(@ModelAttribute("userForm")UserRoleForm userRoleForm, BindingResult results,Model model)
    {
        logger.info("Users - Search Last Page button Clicked");
        userRoleForm.setPageNumber(userRoleForm.getTotalPageCount()-1);
        priorSearch(userRoleForm);
        model.addAttribute(RecapConstants.TEMPLATE,RecapConstants.USER_ROLES);
        return new ModelAndView("userRolesSearch","userRoleForm",userRoleForm);
    }

    private void priorSearch(UserRoleForm userRoleForm){
        List<Object> roles=userRoleService.getRoles(UserManagement.SUPER_ADMIN.getIntegerValues());
        List<Object> institutions=userRoleService.getInstitutions(false,1);
        userRoleForm.setRoles(roles);
        userRoleForm.setInstitutions(institutions);
        userRoleForm.setAllowCreateEdit(true);
        searchAndSetResult(userRoleForm,false,1);
    }



    private void searchAndSetResult(UserRoleForm userRoleForm,boolean superAdmin,Integer userId)
    {
        if(StringUtils.isBlank(userRoleForm.getSearchNetworkId()) && StringUtils.isBlank(userRoleForm.getRoleName())) {
            logger.debug("Search All Users");
            Page<UsersEntity> usersEntities = userRoleService.searchUsers(userRoleForm, superAdmin);
            userRoleForm.setUserRoleFormList(setFormValues(usersEntities.getContent(), userId));
            userRoleForm.setShowResults(true);
            userRoleForm.setTotalRecordsCount(String.valueOf(userRoleForm.getUserRoleFormList().size()));
        }else if(StringUtils.isNotBlank(userRoleForm.getNetworkLoginId()))
        {
            logger.debug("Search Users By NetworkId :"+userRoleForm.getNetworkLoginId());
            Page<UsersEntity> usersEntities = userRoleService.searchByNetworkId(userRoleForm, superAdmin);
            userRoleForm.setUserRoleFormList(setFormValues(usersEntities.getContent(), userId));
            userRoleForm.setShowResults(true);
            userRoleForm.setTotalRecordsCount(String.valueOf(userRoleForm.getUserRoleFormList().size()));
        }else if(StringUtils.isNotBlank(userRoleForm.getRoleName()))
        {
            logger.debug("Search Users by Role Name :"+userRoleForm.getRoleName());
            Page<RoleEntity> roleEntities = userRoleService.searchByRoleName(userRoleForm, superAdmin);
            userRoleForm.setUserRoleFormList(setValuesFromRole(roleEntities.getContent(), userId,userRoleForm.getInstitutionId()));
            userRoleForm.setShowResults(true);
            userRoleForm.setTotalRecordsCount(String.valueOf(userRoleForm.getUserRoleFormList().size()));
        }else
        {
            userRoleForm.setShowResults(false);
        }


    }

    private List<UserRoleForm> setFormValues(List<UsersEntity> usersEntities,Integer userId)
    {
        List<UserRoleForm> userRoleFormList=new ArrayList<UserRoleForm>();
        appendValues(usersEntities, userRoleFormList,  userId);
        return userRoleFormList;
    }

    private List<UserRoleForm> setValuesFromRole(List<RoleEntity> roleEntities,Integer userId,Integer loginInstitutionId)
    {
        List<UserRoleForm> userRoleFormList=new ArrayList<UserRoleForm>();
        for(RoleEntity rolesEntity:roleEntities)
        {
            appendValues(rolesEntity.getUsers(), userRoleFormList,  userId);
        }
        return userRoleFormList;
    }

    private void appendValues(Collection<UsersEntity> usersEntities, List<UserRoleForm> userRoleFormList, Integer userId)
    {
        for(UsersEntity usersEntity:usersEntities)
        {
            InstitutionEntity institutionEntity=usersEntity.getInstitutionEntity();
            if(!userId.equals(usersEntity.getUserId()) && !usersEntity.getUserId().equals(UserManagement.SUPER_ADMIN.getIntegerValues())) {
                UserRoleForm userRoleDeatailsForm=new UserRoleForm();
                StringBuffer rolesBuffer=new StringBuffer();
                userRoleDeatailsForm.setUserId(usersEntity.getUserId());
                userRoleDeatailsForm.setInstitutionId(institutionEntity.getInstitutionId());
                userRoleDeatailsForm.setInstitutionName(institutionEntity.getInstitutionName());
                userRoleDeatailsForm.setNetworkLoginId(usersEntity.getLoginId());
                for (RoleEntity roleEntity : usersEntity.getUserRole()) {
                    rolesBuffer.append(roleEntity.getRoleName()+",");
                }
                userRoleDeatailsForm.setRoleName(roles(rolesBuffer.toString(),","));
                userRoleFormList.add(userRoleDeatailsForm);//Added all user's details
            }
        }

    }

    private String roles(String rolesBuffer,String seperator)
    {
        if(rolesBuffer!=null && rolesBuffer.endsWith(seperator))
        {
            return rolesBuffer.substring(0,rolesBuffer.length()-1);
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value="/userRoles", method=RequestMethod.POST , params="action=createUser")
    public ModelAndView createUserRequest(@ModelAttribute("userRoleForm") UserRoleForm userRoleForm,Model model)
    {
        logger.info("User - Create Request clicked");
        List<Object> roles=userRoleService.getRoles(1);
        List<Object> institutions=userRoleService.getInstitutions(false,1);
        userRoleForm.setRoles(roles);
        userRoleForm.setInstitutions(institutions);
        userRoleForm.setAllowCreateEdit(true);
        return new ModelAndView("userRolesSearch","userRoleForm",userRoleForm);
    }
}
