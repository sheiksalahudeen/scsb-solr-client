package org.recap.service.partnerservice;

import org.recap.RecapConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by premkb on 14/3/17.
 */
@Service
public class ColumbiaService {

    private static final Logger logger = LoggerFactory.getLogger(ColumbiaService.class);

    @Value("${ils.columbia.bibdata}")
    private String ilsColumbiaBibData;

    /**
     * This method gets bib data response (marc xml) based on the itemBarcode from ILS for Columbia.
     *
     * @param itemBarcode the item barcode
     * @return the bib data
     */
    public String getBibData(String itemBarcode) {
        RestTemplate restTemplate = new RestTemplate();
        HostnameVerifier verifier = new NullHostnameVerifier();
        SCSBSimpleClientHttpRequestFactory factory = new SCSBSimpleClientHttpRequestFactory(verifier);
        restTemplate.setRequestFactory(factory);

        String bibDataResponse = null;
        String response = null;
        try {
            logger.info("BIBDATA URL = "+ilsColumbiaBibData);
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
            HttpEntity requestEntity = new HttpEntity(headers);
            Map<String, String> params = new HashMap<>();
            params.put("barcode", itemBarcode);
            ResponseEntity<String> responseEntity = restTemplate.exchange(ilsColumbiaBibData, HttpMethod.GET, requestEntity, String.class, params);
            bibDataResponse = responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            logger.error(RecapConstants.ITEM_BARCODE_NOT_FOUND);
            response = RecapConstants.ITEM_BARCODE_NOT_FOUND;
            throw new RuntimeException(response);
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            logger.error(RecapConstants.SERVICE_UNAVAILABLE);
            response = ilsColumbiaBibData + RecapConstants.SERVICE_UNAVAILABLE;
            throw new RuntimeException(response);
        }
        return bibDataResponse;
    }
}
