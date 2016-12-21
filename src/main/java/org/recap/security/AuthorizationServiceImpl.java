package org.recap.security;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.recap.model.jpa.PermissionEntity;
import org.recap.model.jpa.RoleEntity;
import org.recap.model.jpa.UsersEntity;
import org.recap.repository.jpa.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dharmendrag on 21/12/16.
 */
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    public UserDetailsRepository getUserDetailsRepository() {
        return userDetailsRepository;
    }

    public void setUserDetailsRepository(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    public AuthorizationInfo doAuthorizationInfo(SimpleAuthorizationInfo authorizationInfo, Integer loginId)
    {
        UsersEntity usersEntity = userDetailsRepository.findByUserId(loginId);
        String spliter=UserManagement.TOKEN_SPLITER.getValue();
        String institution=null;
        if (usersEntity == null) {
            return null;
        } else {
            for (RoleEntity role : usersEntity.getUserRole()) {
                institution=usersEntity.getInstitutionEntity().getInstitutionCode();
                authorizationInfo.addRole(role.getRoleName());
                if(role.getRoleName().equals(UserManagement.ReCAP.getValue()))
                {
                    institution="*";
                }
                for (PermissionEntity permissionEntity : role.getPermissions()) {
                    //authorizationInfo.addStringPermission(permissionEntity.getPermissionName()+spliter+institution);
                    authorizationInfo.addStringPermission(permissionEntity.getPermissionName());
                }
            }
        }
        return authorizationInfo;
    }
}
