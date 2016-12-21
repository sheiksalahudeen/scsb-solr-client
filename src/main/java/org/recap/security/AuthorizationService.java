package org.recap.security;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;

/**
 * Created by dharmendrag on 21/12/16.
 */
public interface AuthorizationService {

    AuthorizationInfo doAuthorizationInfo(SimpleAuthorizationInfo authorizationInfo, Integer loginId);
}
