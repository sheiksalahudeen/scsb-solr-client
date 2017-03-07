package org.recap.service.partnerservice;

import org.recap.RecapConstants;
import org.recap.service.authorization.NyplOauthTokenApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by premkb on 18/12/16.
 */
@Service
public class NYPLService {

    private static final Logger logger = LoggerFactory.getLogger(NYPLService.class);

    @Value("${ils.nypl.bibdata}")
    String ilsNYPLBibData;

    @Value("${ils.nypl.bibdata.parameter}")
    String ilsNYPLBibDataParameter;

    @Autowired
    NyplOauthTokenApiService nyplOauthTokenApiService;

    public String getBibData(String itemBarcode, String customerCode) {
        RestTemplate restTemplate = new RestTemplate();
        String bibDataResponse = null;
        String response = null;
        try {
            String authorization = "Bearer " + nyplOauthTokenApiService.generateAccessTokenForNyplApi();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
            headers.set("Authorization", authorization);
            HttpEntity requestEntity = new HttpEntity(headers);
            Map<String, String> params  = new HashMap<>();
            params.put("barcode", itemBarcode);
            params.put("customercode", customerCode);
            String url = ilsNYPLBibData + ilsNYPLBibDataParameter;
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class, params);
            bibDataResponse = responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            response = "Item Barcode not found";
            throw new RuntimeException(response);
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            response = ilsNYPLBibData + " Service is Unavailable.";
            throw new RuntimeException(response);
        }
        return bibDataResponse;
    }

}
