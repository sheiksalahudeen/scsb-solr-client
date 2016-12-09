package org.recap.security.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.recap.model.jpa.RoleEntity;
import org.recap.model.jpa.UsersEntity;
import org.recap.model.userManagement.UserForm;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dharmendrag on 29/11/16.
 */
@Component
public class SimpleAuthorizationRealm extends AuthorizingRealm{

    @Autowired
    protected UserDetailsRepository userRepo;

    @Autowired
    protected UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserDetailsRepository getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserDetailsRepository userRepo) {
        this.userRepo = userRepo;
    }

    public SimpleAuthorizationRealm(){
        setName("simpleAuthRealm");
        setCredentialsMatcher(new SimpleCredentialsMatcher());
        setCachingEnabled(true);
    }



    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Integer loginId=(Integer)principals.fromRealm(getName()).iterator().next();
        System.out.println("Login Id ---->"+loginId);
        Set<String> permissionSet=new HashSet<String>();

        UsersEntity usersEntity = userRepo.findByUserId(loginId);
        if( usersEntity != null ) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            for( RoleEntity role :usersEntity.getUserRole()) {
                info.addRole(role.getRoleName());
                info.addStringPermissions( role.getPermissions() );
            }

            return info;
        } else {
            return null;
        }

    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        UsernamePasswordToken token=(UsernamePasswordToken)authToken;
        UserForm userForm=null;
        try {
            userForm = userService.toUserForm(userRepo.findByLoginId(token.getUsername()), userForm);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        if(userForm!=null){
            return new SimpleAuthenticationInfo(userForm.getUserId(),userForm.getPassword(),getName());
        }
        return null;
    }



}
