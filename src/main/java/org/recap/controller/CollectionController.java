package org.recap.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.search.CollectionForm;
import org.recap.model.search.SearchItemResultRow;
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
                                       Model model) throws Exception {
        searchAndSetResults(collectionForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.COLLECTION);
        return new ModelAndView("searchRecords", "collectionForm", collectionForm);
    }

    private void searchAndSetResults(CollectionForm collectionForm) throws Exception {
        trimBarcodes(collectionForm);
        limitBarcodes(collectionForm);
        buildResultRows(collectionForm);
        buildMissingBarcodes(collectionForm);
    }

    private void limitBarcodes(CollectionForm collectionForm) {
        String[] barcodeArray = collectionForm.getItemBarcodes().split(",");
        if (barcodeArray.length > RecapConstants.BARCODE_LIMIT) {
            barcodeArray = Arrays.copyOfRange(barcodeArray, 0, RecapConstants.BARCODE_LIMIT);
            collectionForm.setErrorMessage(RecapConstants.BARCODE_LIMIT_ERROR);
        }
        collectionForm.setItemBarcodes(StringUtils.join(barcodeArray, ","));
    }

    private void trimBarcodes(CollectionForm collectionForm) {
        List<String> barcodeList = new ArrayList<>();
        String[] barcodeArray = collectionForm.getItemBarcodes().split(",");
        for (String barcode : barcodeArray) {
            if (StringUtils.isNotBlank(barcode)) {
                String itemBarcode = barcode.trim();
                barcodeList.add(itemBarcode);
            }
        }
        collectionForm.setItemBarcodes(StringUtils.join(barcodeList, ","));
    }

    private void buildResultRows(CollectionForm collectionForm) throws Exception {
        if (StringUtils.isNotBlank(collectionForm.getItemBarcodes())) {
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
        } else {
            collectionForm.setErrorMessage(RecapConstants.NO_RESULTS_FOUND);
        }
    }

    private void buildMissingBarcodes(CollectionForm collectionForm) {
        Set<String> missingBarcodes = getMissingBarcodes(collectionForm);
        if (CollectionUtils.isNotEmpty(missingBarcodes)) {
            String errorMessage = (StringUtils.isNotBlank(collectionForm.getErrorMessage()) ? collectionForm.getErrorMessage() + System.lineSeparator() : "") + RecapConstants.BARCODES_NOT_FOUND + " - " + StringUtils.join(missingBarcodes, ",");
            collectionForm.setErrorMessage(errorMessage);
        }
    }

    private Set<String> getMissingBarcodes(CollectionForm collectionForm) {
        if (StringUtils.isNotBlank(collectionForm.getItemBarcodes())) {
            String[] barcodeArray = collectionForm.getItemBarcodes().split(",");
            Set<String> missingBarcodes = new HashSet<>(Arrays.asList(barcodeArray));
            for (SearchResultRow searchResultRow : collectionForm.getSearchResultRows()) {
                String barcode = searchResultRow.getBarcode();
                if (StringUtils.isBlank(barcode)) {
                    if (CollectionUtils.isNotEmpty(searchResultRow.getSearchItemResultRows())) {
                        SearchItemResultRow searchItemResultRow = searchResultRow.getSearchItemResultRows().get(0);
                        barcode = searchItemResultRow.getBarcode();
                        searchResultRow.setBarcode(barcode);
                        searchResultRow.setItemId(searchItemResultRow.getItemId());
                        searchResultRow.setCollectionGroupDesignation(searchItemResultRow.getCollectionGroupDesignation());
                    }
                }
                missingBarcodes.remove(barcode);
            }
            return missingBarcodes;
        }
        return Collections.EMPTY_SET;
    }
}
