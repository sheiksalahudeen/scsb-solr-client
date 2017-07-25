package org.recap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.transfer.HoldingTransferResponse;
import org.recap.model.transfer.ItemTransferResponse;
import org.recap.model.transfer.TransferRequest;
import org.recap.model.transfer.TransferResponse;
import org.recap.service.transfer.TransferService;
import org.recap.util.HelperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;

/**
 * Created by sheiks on 12/07/17.
 */
@RestController
@RequestMapping("/transfer")
public class TransferController {

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    @Autowired
    private TransferService transferService;

    @Autowired
    private HelperUtil helperUtil;

    private ObjectMapper objectMapper;

    /**
     * This method is transfer the item or holdings from one bib to another bib.
     *
     * @return response
     * @throws Exception
     */
    @RequestMapping(value = "/processTransfer",method = RequestMethod.POST)
    public TransferResponse processTransfer(@RequestBody TransferRequest transferRequest) {
        // todo: validate transfer request
        TransferResponse transferResponse = new TransferResponse();
        String institution = transferRequest.getInstitution();

        int successCount = 0;
        int failureCount = 0;

        InstitutionEntity institutionEntity = null;
        if(StringUtils.isBlank(institution)) {
            transferResponse.setMessage(RecapConstants.TRANSFER.INSTITUION_EMPTY);
            institution = RecapConstants.UNKNOWN_INSTITUTION;
            String requestString = helperUtil.getJsonString(transferRequest);
            String responseString = transferResponse.getMessage();
            transferService.saveReportForTransfer(requestString, responseString, institution, RecapConstants.TRANSFER.ROOT);
            return transferResponse;
        } else {
            institutionEntity = transferService.getInstitutionDetailsRepository().findByInstitutionCode(institution);
            if(null == institutionEntity) {
                transferResponse.setMessage(RecapConstants.TRANSFER.UNKNOWN_INSTITUTION);
                institution = RecapConstants.UNKNOWN_INSTITUTION;
                String requestString = helperUtil.getJsonString(transferRequest);
                String responseString = transferResponse.getMessage();
                transferService.saveReportForTransfer(requestString, responseString, institution, RecapConstants.TRANSFER.ROOT);
                return transferResponse;
            }
        }

        List<HoldingTransferResponse> holdingTransferResponses = transferService.processHoldingTransfer(transferRequest, institutionEntity);
        transferResponse.setHoldingTransferResponses(holdingTransferResponses);

        if(CollectionUtils.isNotEmpty(holdingTransferResponses)) {
            for (Iterator<HoldingTransferResponse> iterator = holdingTransferResponses.iterator(); iterator.hasNext(); ) {
                HoldingTransferResponse holdingTransferResponse = iterator.next();
                if(holdingTransferResponse.isSuccess()) {
                    successCount++;
                } else {
                    failureCount++;
                }
            }
        }

        List<ItemTransferResponse> itemTransferResponses = transferService.processItemTransfer(transferRequest, institutionEntity);
        transferResponse.setItemTransferResponses(itemTransferResponses);

        if(CollectionUtils.isNotEmpty(itemTransferResponses)) {
            for (Iterator<ItemTransferResponse> iterator = itemTransferResponses.iterator(); iterator.hasNext(); ) {
                ItemTransferResponse itemTransferResponse = iterator.next();
                if(itemTransferResponse.isSuccess()) {
                    successCount++;
                } else {
                    failureCount++;
                }
            }
        }


        if(successCount > 0 && failureCount > 0) {
            transferResponse.setMessage(RecapConstants.TRANSFER.PARTIALLY_SUCCESS);
            return transferResponse;
        } else if(successCount > 0) {
            transferResponse.setMessage(RecapConstants.TRANSFER.COMPLETED);
            return transferResponse;
        } else if(failureCount > 0) {
            transferResponse.setMessage(RecapConstants.TRANSFER.FAILED);
            return transferResponse;
        }

        return transferResponse;

    }
}
