package org.recap.security;

import org.apache.shiro.SecurityUtils;
import org.recap.model.jpa.PermissionEntity;
import org.recap.model.userManagement.UserForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.PermissionsRepository;
import org.recap.repository.jpa.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dharmendrag on 29/11/16.
 */
@Transactional
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private PermissionsRepository permissionsRepository;

    public PermissionsRepository getPermissionsRepository() {
        return permissionsRepository;
    }

    public void setPermissionsRepository(PermissionsRepository permissionsRepository) {
        this.permissionsRepository = permissionsRepository;
    }

    public InstitutionDetailsRepository getInstitutionDetailsRepository() {
        return institutionDetailsRepository;
    }

    public void setInstitutionDetailsRepository(InstitutionDetailsRepository institutionDetailsRepository) {
        this.institutionDetailsRepository = institutionDetailsRepository;
    }

    public UserDetailsRepository getUserDetails() {
        return userDetailsRepository;
    }

    public void setUserDetails(UserDetailsRepository userDetails) {
        this.userDetailsRepository = userDetails;
    }

    public UserForm findUser(String loginId,UserForm userForm)throws Exception
    {
        return UserManagement.toUserForm(userDetailsRepository.findByLoginId(loginId),userForm);
    }


    public UserForm getCurrentUser()
    {
        final Integer currentUserId=(Integer) SecurityUtils.getSubject().getPrincipal();
        try {
            if (currentUserId != null) {
                return findUserById(currentUserId);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public UserForm findUserById(Integer userId) throws Exception {
        return UserManagement.toUserForm(userDetailsRepository.findByUserId(userId),new UserForm());
    }

    public Map<Integer,String> getPermissions()
    {
        Map<Integer,String> permissionsMap=new HashMap<Integer,String>();
        List<PermissionEntity> permissions=permissionsRepository.findAll();

        for(PermissionEntity permissionEntity:permissions)
        {
            permissionsMap.put(permissionEntity.getPermissionId(),permissionEntity.getPermissionName());
        }

        return permissionsMap;
    }






}
