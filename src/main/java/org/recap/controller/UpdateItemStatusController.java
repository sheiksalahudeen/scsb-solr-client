package org.recap.controller;

import org.recap.model.jpa.ItemEntity;
import org.recap.util.UpdateCgdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudhishk on 17/1/17.
 */
@RestController
@RequestMapping("/updateItem")
public class UpdateItemStatusController {


    private Logger logger = LoggerFactory.getLogger(UpdateCgdRestController.class);

    @Autowired
    UpdateCgdUtil updateCgdUtil;

    @RequestMapping(value = "/updateItemAvailablityStatus", method = RequestMethod.GET)
    public String updateCgdForItem(@RequestParam String itemBarcode) {
        String statusMessage = null;
        try {
            updateCgdUtil.updateCGDForItemInSolr(itemBarcode, new ArrayList<>());
            statusMessage = "Solr Indexing Successful";
        } catch (Exception e) {
            statusMessage = "Solr Indexing Failed";
            logger.error(e.getMessage());
        }
        return statusMessage;
    }
}
