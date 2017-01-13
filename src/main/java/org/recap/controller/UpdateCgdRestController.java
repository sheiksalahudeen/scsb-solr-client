package org.recap.controller;

import org.recap.util.UpdateCgdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private Logger logger = LoggerFactory.getLogger(UpdateCgdRestController.class);

    @Autowired
    UpdateCgdUtil updateCgdUtil;

    @RequestMapping(value="/updateCgd", method = RequestMethod.GET)
    public String updateCgdForItem(@RequestParam Integer itemId, @RequestParam String newCollectionGroupDesignation, @RequestParam String cgdChangeNotes) {
        String statusMessage = null;
        try {
            statusMessage = updateCgdUtil.updateCGDForItem(itemId, newCollectionGroupDesignation, cgdChangeNotes);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return statusMessage;
    }
}
