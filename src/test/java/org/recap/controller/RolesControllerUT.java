package org.recap.controller;

import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.model.jpa.PermissionEntity;
import org.recap.model.jpa.RoleEntity;
import org.recap.model.search.RolesForm;
import org.recap.repository.jpa.PermissionsRepository;
import org.recap.repository.jpa.RolesDetailsRepositorty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 23/12/16.
 */
public class RolesControllerUT extends BaseTestCase{

    @Mock
    Model model;

    @Mock
    BindingResult bindingResult;

    @Autowired
    RolesController rolesController;

    @Autowired
    PermissionsRepository permissionsRepository;

    @Autowired
    RolesDetailsRepositorty rolesDetailsRepositorty;

    @Test
    public void testRoles(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("SuperAdmin");
        ModelAndView modelAndView = rolesController.search(rolesForm, bindingResult, model);
        assertNotNull(modelAndView);
        Map rolesMap = new HashMap();
        rolesMap = modelAndView.getModel();
        rolesForm = (RolesForm) rolesMap.get("rolesForm");
        assertEquals(rolesForm.getRolesSearchResults().get(0).getRolesDescription(),"Admin for all the institutions");
    }

    @Test
    public void testCreateAndDeleteRole(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setNewRoleName("NewSearch");
        rolesForm.setNewRoleDescription("Ability to search SCSB and export results");
        rolesForm.setNewPermissionNames("CreateUser");
        ModelAndView modelAndView = rolesController.newRole(rolesForm, bindingResult, model);
        assertNotNull(modelAndView);
        Map rolesMap = new HashMap();
        rolesMap = modelAndView.getModel();
        rolesForm = (RolesForm) rolesMap.get("rolesForm");
        assertEquals(rolesForm.getMessage(),"Role added successfully");

        RoleEntity roleEntity = rolesDetailsRepositorty.findByRoleName("NewSearch");
        assertNotNull(roleEntity);
        assertNotNull(roleEntity.getRoleId());

        RolesForm rolesForm1 = new RolesForm();
        rolesForm1.setRoleId(roleEntity.getRoleId());
        ModelAndView modelAndView1 = rolesController.delete(rolesForm1,bindingResult, model);
        assertNotNull(modelAndView1);
        rolesMap.clear();
        rolesMap = modelAndView1.getModel();
        rolesForm1 = (RolesForm) rolesMap.get("rolesForm");
        assertEquals(rolesForm1.getMessage(),"Role deleted successfully");

    }



}