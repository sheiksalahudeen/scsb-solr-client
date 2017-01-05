package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.model.jpa.PermissionEntity;
import org.recap.model.jpa.RoleEntity;
import org.recap.model.search.RolesForm;
import org.recap.model.search.RolesSearchResult;
import org.recap.repository.jpa.PermissionsRepository;
import org.recap.repository.jpa.RolesDetailsRepositorty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hemalathas on 22/12/16.
 */
@Controller
public class RolesController {

    Logger logger = LoggerFactory.getLogger(RolesController.class);

    @Autowired
    RolesDetailsRepositorty rolesDetailsRepositorty;

    @Autowired
    PermissionsRepository permissionsRepository;

    @RequestMapping("/roles")
    public String collection(Model model) {
        RolesForm rolesForm = new RolesForm();
        model.addAttribute("rolesForm", rolesForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.ROLES);
        return "searchRecords";
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=searchRoles")
    public ModelAndView search(@Valid @ModelAttribute("rolesForm") RolesForm rolesForm,
                               BindingResult result,
                               Model model) {
        rolesForm.setShowResults(true);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.ROLES);
        setRolesFormSearchResults(rolesForm);
        return new ModelAndView("searchRecords", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=populatePermissionName")
    public ModelAndView populatePermissionNames(Model model) {
        RolesForm rolesForm = getAllPermissionNames();
        model.addAttribute("rolesForm", rolesForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.ROLES);
        return new ModelAndView("roles", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=loadCreateRole")
    public ModelAndView newRole(@Valid @ModelAttribute("rolesForm") RolesForm rolesForm,
                               BindingResult result,
                               Model model) {

        RoleEntity roleEntity = saveNewRoleToDB(rolesForm);
        if(null != roleEntity){
            rolesForm.setMessage("Role added successfully");
        }else{
            rolesForm.setErrorMessage("Roles should not be duplicate");
        }
        return new ModelAndView("roles", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=editRole")
    public ModelAndView editRole(Integer roleId, String roleName, String roleDescription, String permissionName) {
        RolesForm rolesForm = getAllPermissionNames();
        rolesForm.setRoleId(roleId);
        rolesForm.setEditRoleName(roleName);
        rolesForm.setEditRoleDescription(roleDescription);
        rolesForm.setEditPermissionNames(permissionName);
        rolesForm.setSelectedPermissionNames(getSeletedPermissionNames(permissionName));
        return new ModelAndView("roles", "rolesForm", rolesForm);

        //return new ModelAndView("searchRecords", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=saveEditedRole")
    public ModelAndView saveEditedRole(@Valid @ModelAttribute("rolesForm") RolesForm rolesForm,
                                BindingResult result,
                                Model model) {


            RoleEntity roleEntity = saveEditedRoleToDB(rolesForm);
            if(null != roleEntity){
                rolesForm.setMessage("Role edited and saved successfully");
            }
            else{
            rolesForm.setErrorMessage("Roles should not be duplicate");
        }
        return new ModelAndView("roles", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=deleteRole")
    public ModelAndView deleteRole(Integer roleId,String roleName, String roleDescription, String permissionName) {
        RolesForm rolesForm = getAllPermissionNames();
        rolesForm.setRoleId(roleId);
        rolesForm.setRoleNameForDelete(roleName);
        rolesForm.setRoleDescriptionForDelete(roleDescription);
        rolesForm.setPermissionNamesForDelete(permissionName);
        rolesForm.setSelectedPermissionNames(getSeletedPermissionNames(permissionName));
        return new ModelAndView("roles", "rolesForm", rolesForm);

        //return new ModelAndView("searchRecords", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=delete")
    public ModelAndView delete(@Valid @ModelAttribute("rolesForm") RolesForm rolesForm,
                                       BindingResult result,
                                       Model model) {

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(rolesForm.getRoleId());
        try {
            rolesDetailsRepositorty.delete(roleEntity);
            rolesForm.setMessage("Role deleted successfully");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new ModelAndView("roles", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=previous")
    public ModelAndView searchPrevious(@Valid @ModelAttribute("rolesForm") RolesForm rolesForm,
                                       BindingResult result,
                                       Model model) {
        rolesForm.setShowResults(true);
        findByPagination(rolesForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.ROLES);
        return new ModelAndView("searchRecords", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=next")
    public ModelAndView searchNext(@Valid @ModelAttribute("rolesForm") RolesForm rolesForm,
                                   BindingResult result,
                                   Model model) {
        rolesForm.setShowResults(true);
        findByPagination(rolesForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.ROLES);
        return new ModelAndView("searchRecords", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=first")
    public ModelAndView searchFirst(@Valid @ModelAttribute("rolesForm") RolesForm rolesForm,
                                    BindingResult result,
                                    Model model) {
        rolesForm.setShowResults(true);
        rolesForm.resetPageNumber();
        findByPagination(rolesForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.ROLES);
        return new ModelAndView("searchRecords", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=last")
    public ModelAndView searchLast(@Valid @ModelAttribute("rolesForm") RolesForm rolesForm,
                                   BindingResult result,
                                   Model model) {
        rolesForm.setShowResults(true);
        rolesForm.setPageNumber(rolesForm.getTotalPageCount() - 1);
        findByPagination(rolesForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.ROLES);
        return new ModelAndView("searchRecords", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=clearPage")
    public ModelAndView clearPage(@Valid @ModelAttribute("rolesForm") RolesForm rolesForm,
                                   BindingResult result,
                                   Model model) {
        rolesForm.setNewRoleName("");
        rolesForm.setNewRoleDescription("");
        rolesForm.setNewPermissionNames("");
        rolesForm.setEditRoleName("");
        rolesForm.setEditRoleDescription("");
        rolesForm.setEditPermissionNames("");
        rolesForm.setPermissionNameList(getAllPermissionNames().getPermissionNameList());
        rolesForm.setErrorMessage("");
        rolesForm.setMessage("");
        rolesForm.setSelectedPermissionNames(new ArrayList<String>());
        return new ModelAndView("roles", "rolesForm", rolesForm);
    }

    @ResponseBody
    @RequestMapping(value = "/roles", method = RequestMethod.POST, params = "action=pageSizeChange")
    public ModelAndView onPageSizeChange(@Valid @ModelAttribute("rolesForm") RolesForm rolesForm,
                                         BindingResult result,
                                         Model model) throws Exception {
        rolesForm.setShowResults(true);
        rolesForm.setPageNumber(0);
        findByPagination(rolesForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.ROLES);
        return new ModelAndView("searchRecords", "rolesForm", rolesForm);
    }

    public void setRolesFormSearchResults(RolesForm rolesForm){
        List<RolesSearchResult> rolesSearchResults = new ArrayList<>();
        rolesForm.reset();
        rolesForm.resetPageNumber();
        if(!StringUtils.isEmpty(rolesForm.getRoleName()) && StringUtils.isEmpty(rolesForm.getPermissionNames())){
            if(isSpecialCharacterCheck(rolesForm.getRoleName())){
                Pageable pageable = new PageRequest(rolesForm.getPageNumber(), rolesForm.getPageSize());
                Page<RoleEntity> rolesEntityListByPagination = rolesDetailsRepositorty.findByRoleName(pageable,rolesForm.getRoleName());
                List<RoleEntity> roleEntityList = rolesEntityListByPagination.getContent();
                rolesForm.setTotalRecordCount(NumberFormat.getNumberInstance().format(rolesEntityListByPagination.getTotalElements()));
                rolesForm.setTotalPageCount(rolesEntityListByPagination.getTotalPages());
                if(null != roleEntityList && roleEntityList.size()>0){
                    for(RoleEntity roleEntity : roleEntityList){
                        rolesForm.setTotalRecordCount(String.valueOf(1));
                        RolesSearchResult rolesSearchResult = getRolesSearchResult(roleEntity);
                        rolesSearchResults.add(rolesSearchResult);
                        rolesForm.setRolesSearchResults(rolesSearchResults);
                    }
                }else{
                    rolesForm.setErrorMessage(RecapConstants.INVALID_ROLE_NAME);
                }
            }else{
                rolesForm.setErrorMessage(RecapConstants.SPECIAL_CHARACTERS_NOT_ALLOWED);
            }
        }else if(!StringUtils.isEmpty(rolesForm.getPermissionNames()) && StringUtils.isEmpty(rolesForm.getRoleName())){
            if(isSpecialCharacterCheck(rolesForm.getPermissionNames())){
                Pageable pageable = new PageRequest(rolesForm.getPageNumber(), rolesForm.getPageSize());
                Page<PermissionEntity> rolesEntityListByPagination = permissionsRepository.findByPermissionName(pageable,rolesForm.getPermissionNames());
                List<PermissionEntity> permissionEntityList = rolesEntityListByPagination.getContent();
                rolesForm.setTotalRecordCount(NumberFormat.getNumberInstance().format(rolesEntityListByPagination.getTotalElements()));
                rolesForm.setTotalPageCount(rolesEntityListByPagination.getTotalPages());
                int count = 0;
                if(null != permissionEntityList && permissionEntityList.size()>0){
                    for(PermissionEntity permissionEntity : permissionEntityList){
                        for(RoleEntity roleEntity : permissionEntity.getRoleEntityList()){
                            count = count+1;
                            RolesSearchResult rolesSearchResult = getRolesSearchResult(roleEntity);
                            rolesSearchResults.add(rolesSearchResult);
                        }
                        rolesForm.setRolesSearchResults(rolesSearchResults);
                    }
                    rolesForm.setTotalRecordCount(String.valueOf(count));

                }else{
                    rolesForm.setErrorMessage(RecapConstants.INVALID_PERMISSION);
                }
            }else{
                rolesForm.setErrorMessage(RecapConstants.SPECIAL_CHARACTERS_NOT_ALLOWED);
            }
        }else if(!StringUtils.isEmpty(rolesForm.getRoleName()) && !StringUtils.isEmpty(rolesForm.getPermissionNames())){
            if(isSpecialCharacterCheck(rolesForm.getPermissionNames())){
                RoleEntity roleEntity = rolesDetailsRepositorty.findByRoleName(rolesForm.getRoleName());
                if(null != roleEntity){
                    boolean isExist = true;
                    StringBuffer permission = new StringBuffer();
                    RolesSearchResult rolesSearchResult = new RolesSearchResult();
                    for(PermissionEntity permissionEnt : roleEntity.getPermissions()){
                        if(!rolesForm.getPermissionNames().equalsIgnoreCase(permissionEnt.getPermissionName())){
                            isExist = false;
                        }
                        permission.append(permissionEnt.getPermissionName());permission.append(",");
                    }
                    if(isExist){
                        rolesSearchResult.setPermissionName(permission.toString());
                        rolesSearchResult.setRolesName(roleEntity.getRoleName());
                        rolesSearchResult.setRolesDescription(roleEntity.getRoleDescription());
                        rolesSearchResults.add(rolesSearchResult);
                        rolesForm.setRolesSearchResults(rolesSearchResults);
                    }else{
                        rolesForm.setErrorMessage(RecapConstants.WRONG_PERMISSION);
                    }
                }else{
                    rolesForm.setErrorMessage(RecapConstants.INVALID_ROLE_NAME);
                }
            }else{
                rolesForm.setErrorMessage(RecapConstants.INVALID_ROLE_NAME);
            }
        }else{
            findByPagination(rolesForm);
        }
    }

    private List<String> getSeletedPermissionNames(String permissionName) {
        List<String> permissionNames = new ArrayList<>();
        if(!StringUtils.isEmpty(permissionName)){
            StringTokenizer stringTokenizer = new StringTokenizer(permissionName, ",");
            while(stringTokenizer.hasMoreTokens()){
                String token = stringTokenizer.nextToken();
                String trim = token.trim();
                if(!StringUtils.isEmpty(trim)){
                    permissionNames.add(trim);
                }
            }
        }
        return permissionNames;
    }


    public RolesSearchResult getRolesSearchResult(RoleEntity roleEntity){
        StringBuffer permission = new StringBuffer();
        RolesSearchResult rolesSearchResult = new RolesSearchResult();
        for(PermissionEntity permissionEntity : roleEntity.getPermissions()){
            permission.append(permissionEntity.getPermissionName());permission.append(", ");
        }
        String permissionName = getSeletedPermissionNames(permission.toString()).toString().replaceAll("\\[", "").replaceAll("\\]", "");
        rolesSearchResult.setPermissionName(permissionName);
        rolesSearchResult.setRolesName(roleEntity.getRoleName());
        rolesSearchResult.setRolesDescription(roleEntity.getRoleDescription());
        rolesSearchResult.setRoleId(roleEntity.getRoleId());
        return rolesSearchResult;
    }


    private boolean isSpecialCharacterCheck(String inputString) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(inputString);
        if(!matcher.matches()){
            return false;
        }else{
            return true;
        }
    }

    public void findByPagination(RolesForm rolesForm){
        List<RolesSearchResult> rolesSearchResults = new ArrayList<>();
        Pageable pageable = new PageRequest(rolesForm.getPageNumber(), rolesForm.getPageSize());
        Page<RoleEntity> rolesEntityListByPagination = rolesDetailsRepositorty.findAll(pageable);
        List<RoleEntity> rolesEntityList = rolesEntityListByPagination.getContent();
        rolesForm.setTotalRecordCount(NumberFormat.getNumberInstance().format(rolesEntityListByPagination.getTotalElements()));
        rolesForm.setTotalPageCount(rolesEntityListByPagination.getTotalPages());
        for(RoleEntity roleEntity : rolesEntityList){
            RolesSearchResult rolesSearchResult = getRolesSearchResult(roleEntity);
            rolesSearchResults.add(rolesSearchResult);
        }
        rolesForm.setRolesSearchResults(rolesSearchResults);
    }

    public RoleEntity saveNewRoleToDB(RolesForm rolesForm){
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleName(rolesForm.getNewRoleName().trim());
        roleEntity.setRoleDescription(rolesForm.getNewRoleDescription());
        List<String> permissionNameList = splitStringAndGetList(rolesForm.getNewPermissionNames());
        RoleEntity savedRoleEntity = saveRoleEntity(roleEntity, permissionNameList);
        return savedRoleEntity;

    }

    private RoleEntity saveRoleEntity(RoleEntity roleEntity1, List<String> permissionNameList) {
        PermissionEntity permissionEntity;
        RoleEntity roleEntity = null;
        Set rolesSet = new HashSet();
        try {
            for(String permissionName : permissionNameList){
                permissionEntity = permissionsRepository.findByPermissionName(permissionName);
                rolesSet.add(permissionEntity);
            }
            roleEntity1.setPermissions(rolesSet);
            roleEntity = rolesDetailsRepositorty.save(roleEntity1);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return roleEntity;
    }

    public RoleEntity saveEditedRoleToDB(RolesForm rolesForm){
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(rolesForm.getRoleId());
        roleEntity.setRoleName(rolesForm.getEditRoleName().trim());
        roleEntity.setRoleDescription(rolesForm.getEditRoleDescription());
        List<String> permissionNameList = splitStringAndGetList(rolesForm.getEditPermissionNames());
        RoleEntity savedRoleEntity = saveRoleEntity(roleEntity, permissionNameList);
        return savedRoleEntity;
    }

    private List<String> splitStringAndGetList(String inputString) {
        String[] splittedString = inputString.split(",");
        List<String> stringList = Arrays.asList(splittedString);
        return stringList;
    }

    private RolesForm getAllPermissionNames(){
        RolesForm rolesForm = new RolesForm();
        List<String> permissionNameList = new ArrayList<>();
        List<PermissionEntity> permissionEntityList = permissionsRepository.findAll();
        for(PermissionEntity permissionEntity : permissionEntityList){
            String permissionName = permissionEntity.getPermissionName();
            permissionNameList.add(permissionName);
        }
        rolesForm.setPermissionNameList(permissionNameList);
        return rolesForm;
    }

}
