package org.recap.service.authorization;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.service.authorization.NyplOauthTokenApiService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 9/12/16.
 */
public class NyplOauthTokenApiServiceUT extends BaseTestCase {

    @Autowired
    NyplOauthTokenApiService nyplOauthTokenApiService;

    @Test
    public void testGenerateOAuthToken() throws Exception {
        String accessToken = nyplOauthTokenApiService.generateAccessTokenForNyplApi();
        assertNotNull(accessToken);
    }
}
