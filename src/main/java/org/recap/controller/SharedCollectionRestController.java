package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.service.ItemAvailabilityService;
import org.recap.service.accession.AccessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by chenchulakshmig on 6/10/16.
 */
@RestController
@RequestMapping("/sharedCollection")
public class SharedCollectionRestController {

    private Logger logger = LoggerFactory.getLogger(SharedCollectionRestController.class);

    @Autowired
    private ItemAvailabilityService itemAvailabilityService;

    @Autowired
    AccessionService accessionService;

    @Value("${ongoing.accession.input.limit}")
    private Integer inputLimit;

    public ItemAvailabilityService getItemAvailabilityService() {
        return itemAvailabilityService;
    }

    public void setItemAvailabilityService(ItemAvailabilityService itemAvailabilityService) {
        this.itemAvailabilityService = itemAvailabilityService;
    }

    public AccessionService getAccessionService() {
        return accessionService;
    }

    public void setAccessionService(AccessionService accessionService) {
        this.accessionService = accessionService;
    }

    public Integer getInputLimit() {
        return inputLimit;
    }

    public void setInputLimit(Integer inputLimit) {
        this.inputLimit = inputLimit;
    }

    @RequestMapping(value = "/itemAvailabilityStatus", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity itemAvailabilityStatus(@RequestParam String itemBarcode) {
        String itemStatus = null;
        try {
            itemStatus = getItemAvailabilityService().getItemStatusByBarcodeAndIsDeletedFalse(itemBarcode);
        } catch (Exception exception) {
            logger.error(RecapConstants.LOG_ERROR,exception);
            return new ResponseEntity("Scsb Persistence Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (StringUtils.isEmpty(itemStatus)) {
            return new ResponseEntity(RecapConstants.ITEM_BARCDE_DOESNOT_EXIST, getHttpHeaders(), HttpStatus.OK);

        } else {
            return new ResponseEntity(itemStatus, getHttpHeaders(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/accession", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity accession(@RequestBody List<AccessionRequest> accessionRequestList) {
        ResponseEntity responseEntity;
        if (accessionRequestList.size() > getInputLimit()) {
            return new ResponseEntity(RecapConstants.ONGOING_ACCESSION_LIMIT_EXCEED_MESSAGE+inputLimit,getHttpHeaders(),HttpStatus.OK);
        } else {
            String response = getAccessionService().processRequest(accessionRequestList);
            responseEntity = new ResponseEntity(response, getHttpHeaders(), HttpStatus.OK);
        }
        return responseEntity;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(RecapConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }
}
