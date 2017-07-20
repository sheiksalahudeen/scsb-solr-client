package org.recap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

        List<ItemTransferResponse> itemTransferResponses = transferService.processItemTransfer(transferRequest, institutionEntity);
        transferResponse.setItemTransferResponses(itemTransferResponses);

        transferResponse.setMessage(RecapConstants.TRANSFER.COMPLETED);

        return transferResponse;

    }
}
