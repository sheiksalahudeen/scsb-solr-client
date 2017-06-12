package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.util.UpdateCgdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rajeshbabuk on 3/1/17.
 */
@RestController
@RequestMapping("/updateCgdService")
public class UpdateCgdRestController {

    private static final Logger logger = LoggerFactory.getLogger(UpdateCgdRestController.class);

    @Autowired
    private UpdateCgdUtil updateCgdUtil;

    /**
     * This method is used to update cgd for item in both solr and database and sends email notification on successful completion.
     *
     * @param itemBarcode                   the item barcode
     * @param owningInstitution             the owning institution
     * @param oldCollectionGroupDesignation the old collection group designation
     * @param newCollectionGroupDesignation the new collection group designation
     * @param cgdChangeNotes                the cgd change notes
     * @return the string statusMessage
     */
    @RequestMapping(value="/updateCgd", method = RequestMethod.GET)
    public String updateCgdForItem(@RequestParam String itemBarcode, @RequestParam String owningInstitution, @RequestParam String oldCollectionGroupDesignation, @RequestParam String newCollectionGroupDesignation, @RequestParam String cgdChangeNotes) {
        String statusMessage = null;
        try {
            statusMessage = updateCgdUtil.updateCGDForItem(itemBarcode, owningInstitution, oldCollectionGroupDesignation, newCollectionGroupDesignation, cgdChangeNotes);
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return statusMessage;
    }
}
