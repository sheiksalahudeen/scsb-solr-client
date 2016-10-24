package org.recap.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.search.CollectionForm;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchResultRow;
import org.recap.util.SearchRecordsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by rajeshbabuk on 12/10/16.
 */

@Controller
public class CollectionController {

    Logger logger = LoggerFactory.getLogger(CollectionController.class);

    @Autowired
    SearchRecordsUtil searchRecordsUtil;

    @RequestMapping("/collection")
    public String collection(Model model) {
        CollectionForm collectionForm = new CollectionForm();
        model.addAttribute("collectionForm", collectionForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.COLLECTION);
        return "searchRecords";
    }

    @ResponseBody
    @RequestMapping(value = "/collection", method = RequestMethod.POST, params = "action=displayRecords")
    public ModelAndView displayRecords(@Valid @ModelAttribute("collectionForm") CollectionForm collectionForm,
                               BindingResult result,
                               Model model) {
        searchAndSetResults(collectionForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.COLLECTION);
        return new ModelAndView("searchRecords", "collectionForm", collectionForm);
    }

    private void searchAndSetResults(CollectionForm collectionForm) {
        String itemBarcodesString = collectionForm.getItemBarcodes();
        if (StringUtils.isNotBlank(itemBarcodesString)) {
            SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
            searchRecordsRequest.setFieldName(RecapConstants.BARCODE);
            searchRecordsRequest.setFieldValue(collectionForm.getItemBarcodes());

            List<SearchResultRow> searchResultRows = searchRecordsUtil.searchRecords(searchRecordsRequest);
            collectionForm.setSearchResultRows(Collections.EMPTY_LIST);
            if (CollectionUtils.isNotEmpty(searchResultRows)) {
                collectionForm.setSearchResultRows(searchResultRows);
                collectionForm.setShowResults(true);
                collectionForm.setSelectAll(false);
            }

            List<String> missingBarcodes = getMissingBarcodes(collectionForm);
            if (CollectionUtils.isNotEmpty(missingBarcodes)) {
                collectionForm.setErrorMessage(RecapConstants.BARCODES_NOT_FOUND + " - " + StringUtils.join(missingBarcodes, ","));
            }
        } else {
            collectionForm.setErrorMessage(RecapConstants.NO_RESULTS_FOUND);
        }
    }

    private List<String> getMissingBarcodes(CollectionForm collectionForm) {
        String[] barcodes = collectionForm.getItemBarcodes().split(",");
        List<String> missingBarcodes = new ArrayList<>();
        for (String barcode : barcodes) {
            String itemBarcode = barcode.trim();
            missingBarcodes.add(itemBarcode);
        }
        for (SearchResultRow searchResultRow : collectionForm.getSearchResultRows()) {
            String barcode = searchResultRow.getBarcode().trim();
            missingBarcodes.remove(barcode);
        }
        return missingBarcodes;
    }
}
