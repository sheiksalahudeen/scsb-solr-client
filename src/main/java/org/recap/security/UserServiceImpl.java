package org.recap.security;

import org.apache.shiro.SecurityUtils;
import org.recap.model.jpa.UsersEntity;
import org.recap.model.userManagement.UserForm;
import org.recap.repository.jpa.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by dharmendrag on 29/11/16.
 */
@Transactional
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDetailsRepository userDetails;

    public UserDetailsRepository getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetailsRepository userDetails) {
        this.userDetails = userDetails;
    }

    public UserForm findUser(String loginId,UserForm userForm)throws Exception
    {
        return toUserForm(userDetails.findByLoginId(loginId),userForm);
    }
    public UserForm toUserForm(UsersEntity userEntity, UserForm userForm)throws Exception
    {
        try
        {
            if(userForm==null)
            {
                userForm=new UserForm();
            }
            userForm.setUserId(userEntity.getUserId());
            userForm.setUsername(userEntity.getLoginId());
            userForm.setInstitution(userEntity.getInstitutionId());
            userForm.setPassword(userEntity.getPassWord());
            //userForm.setPermissions(userEntity.getUserRole().getPermissions());

        }catch (Exception e)
        {
            throw new Exception(e);
        }
        return userForm;
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
        return toUserForm(userDetails.findByUserId(userId),new UserForm());
    }


}
