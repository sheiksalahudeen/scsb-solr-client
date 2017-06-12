package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.UpdateCgdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by sudhishk on 17/1/17.
 */
@RestController
@RequestMapping("/updateItem")
public class UpdateItemStatusController {


    private static final Logger logger = LoggerFactory.getLogger(UpdateCgdRestController.class);

    @Autowired
    private UpdateCgdUtil updateCgdUtil;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets UpdateCgdUtil object.
     *
     * @return the UpdateCgdUtil object
     */
    public UpdateCgdUtil getUpdateCgdUtil() {
        return updateCgdUtil;
    }

    /**
     * Gets ItemDetailsRepository object.
     *
     * @return the ItemDetailsRepository object.
     */
    public ItemDetailsRepository getItemDetailsRepository() {
        return itemDetailsRepository;
    }

    /**
     * This method is used to update the item availability status in solr for the given itemBarcode.
     *
     * @param itemBarcode the item barcode
     * @return the string statusMessage
     */
    @RequestMapping(value = "/updateItemAvailablityStatus", method = RequestMethod.GET)
    public String updateCgdForItem(@RequestParam String itemBarcode) {
        String statusMessage = null;
        List<ItemEntity> itemEntities = null;
        try {
            itemEntities = getItemDetailsRepository().findByBarcode(itemBarcode);
            getUpdateCgdUtil().updateCGDForItemInSolr(itemEntities);
            statusMessage = "Solr Indexing Successful";
        } catch (Exception e) {
            statusMessage = "Solr Indexing Failed";
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return statusMessage;
    }
}
