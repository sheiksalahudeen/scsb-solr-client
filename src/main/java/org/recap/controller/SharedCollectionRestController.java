package org.recap.controller;

import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapConstants;
import org.recap.model.ItemAvailabilityResponse;
import org.recap.model.ItemAvailabityStatusRequest;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenchulakshmig on 6/10/16.
 */
@RestController
@RequestMapping("/sharedCollection")
public class SharedCollectionRestController {

    private static final Logger logger = LoggerFactory.getLogger(SharedCollectionRestController.class);

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

    @RequestMapping(value = "/itemAvailabilityStatus", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    public List<ItemAvailabilityResponse> itemAvailabilityStatus(@RequestBody ItemAvailabityStatusRequest itemAvailabityStatusRequest) {
        List<ItemAvailabilityResponse> itemAvailabilityResponses = new ArrayList<>();
        ResponseEntity responseEntity;
        try {
            itemAvailabilityResponses=getItemAvailabilityService().getItemStatusByBarcodeAndIsDeletedFalseList(itemAvailabityStatusRequest.getBarcodes());
        } catch (Exception exception) {
            responseEntity = new ResponseEntity("Scsb Persistence Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return itemAvailabilityResponses;
        }
        if (CollectionUtils.isEmpty(itemAvailabilityResponses)) {
            responseEntity = new ResponseEntity(RecapConstants.ITEM_BARCDE_DOESNOT_EXIST, getHttpHeaders(), HttpStatus.OK);
            return itemAvailabilityResponses;
        } else {
            responseEntity = new ResponseEntity(itemAvailabilityResponses, getHttpHeaders(), HttpStatus.OK);
            return itemAvailabilityResponses;
        }
    }

    @RequestMapping(value = "/accession", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity accession(@RequestBody List<AccessionRequest> accessionRequestList) {
        ResponseEntity responseEntity;
        List<AccessionResponse> accessionResponsesList;
        if (accessionRequestList.size() > getInputLimit()) {
            accessionResponsesList = getAccessionResponses();
            return new ResponseEntity(accessionResponsesList,getHttpHeaders(),HttpStatus.OK);
        } else {
            accessionResponsesList =  getAccessionService().processRequest(accessionRequestList);
            responseEntity = new ResponseEntity(accessionResponsesList, getHttpHeaders(), HttpStatus.OK);
        }
        return responseEntity;
    }

    private List<AccessionResponse> getAccessionResponses() {
        List<AccessionResponse> accessionResponsesList;
        accessionResponsesList = new ArrayList<>();
        AccessionResponse accessionResponse = new AccessionResponse();
        accessionResponse.setItemBarcode("");
        accessionResponsesList.add(accessionResponse);
        accessionResponse.setMessage(RecapConstants.ONGOING_ACCESSION_LIMIT_EXCEED_MESSAGE+inputLimit);
        return accessionResponsesList;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(RecapConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }
}
