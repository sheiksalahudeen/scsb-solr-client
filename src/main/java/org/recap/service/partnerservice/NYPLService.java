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
    private String ilsNYPLBibData;

    @Value("${ils.nypl.bibdata.parameter}")
    private String ilsNYPLBibDataParameter;

    @Autowired
    private NyplOauthTokenApiService nyplOauthTokenApiService;

    public NyplOauthTokenApiService getNyplOauthTokenApiService() {
        return nyplOauthTokenApiService;
    }

    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    public String getIlsNYPLBibData() {
        return ilsNYPLBibData;
    }

    public String getIlsNYPLBibDataParameter() {
        return ilsNYPLBibDataParameter;
    }

    public HttpEntity getHttpEntity(HttpHeaders headers){
        return new HttpEntity(headers);
    }

    public HttpHeaders getHttpHeaders(){
        return new HttpHeaders();
    }

    /**
     * This method gets bib data response(scsb xml) based on the itemBarcode and customer code from ILS for NYPL.
     *
     * @param itemBarcode  the item barcode
     * @param customerCode the customer code
     * @return the bib data
     */
    public String getBibData(String itemBarcode, String customerCode) {
        RestTemplate restTemplate = getRestTemplate();
        String bibDataResponse = null;
        String response = null;
        try {
            String authorization = "Bearer " + getNyplOauthTokenApiService().generateAccessTokenForNyplApi();
            HttpHeaders headers = getHttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
            headers.set("Authorization", authorization);
            HttpEntity requestEntity = getHttpEntity(headers);
            Map<String, String> params  = new HashMap<>();
            params.put("barcode", itemBarcode);
            params.put("customercode", customerCode);
            String url = getIlsNYPLBibData() + getIlsNYPLBibDataParameter();
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class, params);
            bibDataResponse = responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            logger.error(RecapConstants.ITEM_BARCODE_NOT_FOUND);
            response = RecapConstants.ITEM_BARCODE_NOT_FOUND;
            throw new RuntimeException(response);
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            response = ilsNYPLBibData + " Service is Unavailable.";
            throw new RuntimeException(response);
        }
        return bibDataResponse;
    }

}
