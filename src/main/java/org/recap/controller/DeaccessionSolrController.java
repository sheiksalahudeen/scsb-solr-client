package org.recap.controller;

import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapConstants;
import org.recap.model.deaccession.DeAccessionSolrRequest;
import org.recap.service.deaccession.DeAccessSolrDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by rajeshbabuk on 15/2/17.
 */
@RestController
@RequestMapping("/deaccessionInSolrService")
public class DeaccessionSolrController {

    @Autowired
    private DeAccessSolrDocumentService deAccessSolrDocumentService;

    /**
     * This method is used to update deaccessioned record in solr.
     *
     * @param deAccessionSolrRequest the de accession solr request
     * @return the response entity
     */
    @RequestMapping(value="/deaccessionInSolr", method = RequestMethod.POST)
    public ResponseEntity deaccessionInSolr(@RequestBody DeAccessionSolrRequest deAccessionSolrRequest) {
        List<Integer> bibIds = deAccessionSolrRequest.getBibIds();
        List<Integer> holdingsIds = deAccessionSolrRequest.getHoldingsIds();
        List<Integer> itemIds = deAccessionSolrRequest.getItemIds();

        if (CollectionUtils.isNotEmpty(bibIds)) {
            deAccessSolrDocumentService.updateIsDeletedBibByBibId(bibIds);
        }
        if (CollectionUtils.isNotEmpty(holdingsIds)) {
            deAccessSolrDocumentService.updateIsDeletedHoldingsByHoldingsId(holdingsIds);
        }
        if (CollectionUtils.isNotEmpty(itemIds)) {
            deAccessSolrDocumentService.updateIsDeletedItemByItemIds(itemIds);
        }
        return new ResponseEntity(RecapConstants.SUCCESS, HttpStatus.OK);
    }
}
