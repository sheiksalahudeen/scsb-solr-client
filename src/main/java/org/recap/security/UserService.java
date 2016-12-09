package org.recap.security;

import org.recap.model.jpa.UsersEntity;
import org.recap.model.userManagement.UserForm;

/**
 * Created by dharmendrag on 29/11/16.
 */

public interface UserService {



    UserForm findUser(String loginId, UserForm userForm)throws Exception;

    UserForm toUserForm(UsersEntity userEntity, UserForm userForm)throws Exception;




}
