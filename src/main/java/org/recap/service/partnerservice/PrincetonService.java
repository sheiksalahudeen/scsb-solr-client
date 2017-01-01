package org.recap.service.partnerservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;

/**
 * Created by premkb on 18/12/16.
 */
@Service
public class PrincetonService {

    @Value("${ils.princeton.bibdata}")
    String ilsprincetonBibData;

    public String getBibData(String itemBarcode) {
        RestTemplate restTemplate = new RestTemplate();
        HostnameVerifier verifier = new NullHostnameVerifier();
        SCSBSimpleClientHttpRequestFactory factory = new SCSBSimpleClientHttpRequestFactory(verifier);
        restTemplate.setRequestFactory(factory);

        String bibDataResponse = null;
        String response = null;
        try {
            bibDataResponse = restTemplate.getForObject(ilsprincetonBibData + itemBarcode, String.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            response = "Item Barcode not found";
            throw new RuntimeException(response);
        } catch (Exception e) {
            e.printStackTrace();
            response = ilsprincetonBibData + " Service is Unavailable.";
            throw new RuntimeException(response);
        }
        return bibDataResponse;
    }
}