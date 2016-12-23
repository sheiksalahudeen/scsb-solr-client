package org.recap.service.partnerservice;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import sun.security.ssl.SSLSocketImpl;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by premkb on 18/12/16.
 */
@Service
public class PrincetonService {

    @Value("${ils.princeton.bibdata}")
    String ilsprincetonBibData;

    public String getBibData(String itemBarcode, String customerCode) {
        RestTemplate restTemplate = new RestTemplate();
        String bibDataResponse = null;
        String response = null;
        try {
            System.setProperty("jsse.enableSNIExtension", "false");
            bibDataResponse = restTemplate.getForObject(ilsprincetonBibData + itemBarcode, String.class);
            System.setProperty("jsse.enableSNIExtension", "true");
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