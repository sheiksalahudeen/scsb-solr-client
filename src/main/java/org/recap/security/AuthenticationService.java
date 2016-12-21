package org.recap.security;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.recap.model.userManagement.UserForm;

/**
 * Created by dharmendrag on 21/12/16.
 */
public interface AuthenticationService {

    UserForm doAuthentication(UsernamePasswordToken token) throws Exception;

}
