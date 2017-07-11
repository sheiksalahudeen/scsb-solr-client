package org.recap.controller;

import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapConstants;
import org.recap.model.BibItemAvailabityStatusRequest;
import org.recap.model.ItemAvailabilityResponse;
import org.recap.model.ItemAvailabityStatusRequest;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.accession.AccessionSummary;
import org.recap.model.jpa.AccessionEntity;
import org.recap.service.ItemAvailabilityService;
import org.recap.service.accession.AccessionService;
import org.recap.service.accession.BulkAccessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @Autowired
    BulkAccessionService bulkAccessionService;

    public BulkAccessionService getBulkAccessionService() {
        return bulkAccessionService;
    }

    @Value("${ongoing.accession.input.limit}")
    private Integer inputLimit;

    /**
     * Gets ItemAvailabilityService object..
     *
     * @return the ItemAvailabilityService object.
     */
    public ItemAvailabilityService getItemAvailabilityService() {
        return itemAvailabilityService;
    }

    /**
     * Gets AccessionService object..
     *
     * @return the AccessionService object.
     */
    public AccessionService getAccessionService() {
        return accessionService;
    }

    /**
     * Gets input limit.
     *
     * @return the input limit
     */
    public Integer getInputLimit() {
        return inputLimit;
    }

    /**
     * This method is used to get the item availability status.
     *
     * @param itemAvailabityStatusRequest the item availabity status request
     * @return the response entity
     */
    @RequestMapping(value = "/itemAvailabilityStatus", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity itemAvailabilityStatus(@RequestBody ItemAvailabityStatusRequest itemAvailabityStatusRequest) {
        List<ItemAvailabilityResponse> itemAvailabilityResponses = new ArrayList<>();
        ResponseEntity responseEntity;
        try {
            itemAvailabilityResponses = getItemAvailabilityService().getItemStatusByBarcodeAndIsDeletedFalseList(itemAvailabityStatusRequest.getBarcodes());
        } catch (Exception exception) {
            responseEntity = new ResponseEntity(RecapConstants.SCSB_PERSISTENCE_SERVICE_IS_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            logger.error(RecapConstants.EXCEPTION, exception);
            return responseEntity;
        }
        responseEntity = new ResponseEntity(itemAvailabilityResponses, getHttpHeaders(), HttpStatus.OK);
        return responseEntity;
    }

    /**
     * This method is used to get the bib availability status.
     *
     * @param bibItemAvailabityStatusRequest the bib item availability status request
     * @return the response entity
     */
    @RequestMapping(value = "/bibAvailabilityStatus", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity bibAvailabilityStatus(@RequestBody BibItemAvailabityStatusRequest bibItemAvailabityStatusRequest) {
        List<ItemAvailabilityResponse> itemAvailabilityResponses;
        ResponseEntity responseEntity;
        itemAvailabilityResponses = getItemAvailabilityService().getbibItemAvaiablityStatus(bibItemAvailabityStatusRequest);
        if (itemAvailabilityResponses.isEmpty()) {
            ItemAvailabilityResponse itemAvailabilityResponse = new ItemAvailabilityResponse();
            itemAvailabilityResponse.setErrorMessage(RecapConstants.BIB_ITEM_DOESNOT_EXIST);
            itemAvailabilityResponses.add(itemAvailabilityResponse);
            responseEntity = new ResponseEntity(itemAvailabilityResponses, getHttpHeaders(), HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity(itemAvailabilityResponses, getHttpHeaders(), HttpStatus.OK);
        }
        return responseEntity;
    }

    /**
     * This method is used to save the accession and send the response.
     *
     * @param accessionRequestList the accession request list
     * @return the response entity
     */
    @RequestMapping(value = "/accessionBatch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity accessionBatch(@RequestBody List<AccessionRequest> accessionRequestList) {
        String responseMessage = getAccessionService().saveRequest(accessionRequestList);
        ResponseEntity responseEntity = new ResponseEntity(responseMessage, getHttpHeaders(), HttpStatus.OK);
        return responseEntity;
    }

    /**
     * This method is used to perform accession for the given list of accessionRequests.
     *
     * @param accessionRequestList the accession request list
     * @return the response entity
     */
    @RequestMapping(value = "/accession", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity accession(@RequestBody List<AccessionRequest> accessionRequestList) {
        ResponseEntity responseEntity;
        List<AccessionResponse> accessionResponsesList;
        if (accessionRequestList.size() > getInputLimit()) {
            accessionResponsesList = getAccessionResponses();
            return new ResponseEntity(accessionResponsesList, getHttpHeaders(), HttpStatus.OK);
        } else {
            String accessionType = RecapConstants.ACCESSION_SUMMARY;
            AccessionSummary accessionSummary = new AccessionSummary(accessionType);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            logger.info("Total record for Accession : {}" , accessionRequestList.size());
            accessionResponsesList = getAccessionService().doAccession(accessionRequestList, accessionSummary);

            stopWatch.stop();
            accessionSummary.setTimeElapsed(stopWatch.getTotalTimeSeconds() + " Secs");

            logger.info(accessionSummary.toString());

            bulkAccessionService.createSummaryReport(accessionSummary.toString(), accessionType);

            responseEntity = new ResponseEntity(accessionResponsesList, getHttpHeaders(), HttpStatus.OK);
        }
        return responseEntity;
    }

    /**
     * This method performs ongoing accession job.
     *
     * @return the string
     */
    @RequestMapping(value = "/ongoingAccessionJob", method = RequestMethod.GET)
    @ResponseBody
    public String ongoingAccessionJob() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String status;
        List<AccessionEntity> accessionEntities = getBulkAccessionService().getAccessionEntities(RecapConstants.PENDING);
        List<AccessionRequest> accessionRequestList = getBulkAccessionService().getAccessionRequest(accessionEntities);
        String accessionType = RecapConstants.BULK_ACCESSION_SUMMARY;
        AccessionSummary accessionSummary = new AccessionSummary(accessionType);
        if(CollectionUtils.isNotEmpty(accessionRequestList)) {
            logger.info("Total record for Bulk Accession : {}" , accessionRequestList.size());
            getBulkAccessionService().doAccession(accessionRequestList, accessionSummary);
            if(accessionSummary.getSuccessRecords() != 0) {
                status = RecapConstants.SUCCESS;
            } else {
                status = RecapConstants.FAILURE;
            }
        } else {
            status = RecapConstants.FAILURE;
        }
        getAccessionService().updateStatusForAccessionEntities(accessionEntities, RecapConstants.COMPLETE_STATUS);
        stopWatch.stop();
        accessionSummary.setTimeElapsed(stopWatch.getTotalTimeSeconds() + " Secs");

        getBulkAccessionService().createSummaryReport(accessionSummary.toString(),accessionType);

        logger.info("Total time taken for processing {} records : {} secs", accessionRequestList.size(), stopWatch.getTotalTimeSeconds());
        logger.info(accessionSummary.toString());

        return status;
    }

    private List<AccessionResponse> getAccessionResponses() {
        List<AccessionResponse> accessionResponsesList;
        accessionResponsesList = new ArrayList<>();
        AccessionResponse accessionResponse = new AccessionResponse();
        accessionResponse.setItemBarcode("");
        accessionResponsesList.add(accessionResponse);
        accessionResponse.setMessage(RecapConstants.ONGOING_ACCESSION_LIMIT_EXCEED_MESSAGE + inputLimit);
        return accessionResponsesList;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(RecapConstants.DATE, new Date().toString());
        return responseHeaders;
    }
}
