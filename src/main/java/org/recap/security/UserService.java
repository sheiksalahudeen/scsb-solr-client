package org.recap.security;

import org.recap.model.userManagement.UserForm;

import java.util.Map;

/**
 * Created by dharmendrag on 29/11/16.
 */

public interface UserService {



    UserForm findUser(String loginId, UserForm userForm)throws Exception;

    Map<Integer,String> getPermissions();






}
