package org.recap.service.authorization;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

/**
 * Created by rajeshbabuk on 9/12/16.
 */
@Service
public class NyplOauthTokenApiService {

    @Value("${ils.nypl.oauth.token.api}")
    private String nyplOauthTokenApiUrl;

    @Value("${ils.nypl.operator.user.id}")
    private String nyplOperatorUserId;

    @Value("${ils.nypl.operator.password}")
    private String nyplOperatorPassword;

    /**
     * This method is used to generate token to access the NYPL SIERRA api using the NYPL operator credentials if they are authorized.
     *
     * @return the string
     * @throws Exception the exception
     */
    public String generateAccessTokenForNyplApi() throws Exception {
        String authorization = "Basic " + new String(Base64Utils.encode((nyplOperatorUserId + ":" + nyplOperatorPassword).getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", authorization);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity("grant_type=client_credentials", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(nyplOauthTokenApiUrl, HttpMethod.POST, requestEntity, String.class);
        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        return (String) jsonObject.get("access_token");
    }
}
