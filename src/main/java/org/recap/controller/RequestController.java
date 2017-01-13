package org.recap.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.marc4j.marc.Record;
import org.recap.RecapConstants;
import org.recap.model.jpa.*;
import org.recap.model.search.RequestForm;
import org.recap.model.search.SearchResultRow;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.recap.util.BibJSONUtil;
import org.recap.util.RequestServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by rajeshbabuk on 13/10/16.
 */

@Controller
public class RequestController {

    Logger logger = LoggerFactory.getLogger(RequestController.class);

    @Autowired
    RequestServiceUtil requestServiceUtil;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Autowired
    CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @RequestMapping("/request")
    public String collection(Model model) {
            RequestForm requestForm = setDefaultsToCreateRequest();
            model.addAttribute("requestForm", requestForm);
            model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.REQUEST);
            return "searchRecords";
    }

    @ResponseBody
    @RequestMapping(value = "/request", method = RequestMethod.POST, params = "action=searchRequests")
    public ModelAndView searchRequests(@Valid @ModelAttribute("requestForm") RequestForm requestForm,
                                       BindingResult result,
                                       Model model) throws Exception {
        requestForm.resetPageNumber();
        searchAndSetResults(requestForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.REQUEST);
        return new ModelAndView("request", "requestForm", requestForm);
    }

    @ResponseBody
    @RequestMapping(value = "/request", method = RequestMethod.POST, params = "action=first")
    public ModelAndView searchFirst(@Valid @ModelAttribute("requestForm") RequestForm requestForm,
                                    BindingResult result,
                                    Model model) {
        requestForm.resetPageNumber();
        searchAndSetResults(requestForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.REQUEST);
        return new ModelAndView("request", "requestForm", requestForm);
    }

    @ResponseBody
    @RequestMapping(value = "/request", method = RequestMethod.POST, params = "action=last")
    public ModelAndView searchLast(@Valid @ModelAttribute("requestForm") RequestForm requestForm,
                                    BindingResult result,
                                    Model model) {
        requestForm.setPageNumber(requestForm.getTotalPageCount() - 1);
        searchAndSetResults(requestForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.REQUEST);
        return new ModelAndView("request", "requestForm", requestForm);
    }

    @ResponseBody
    @RequestMapping(value = "/request", method = RequestMethod.POST, params = "action=previous")
    public ModelAndView searchPrevious(@Valid @ModelAttribute("requestForm") RequestForm requestForm,
                                   BindingResult result,
                                   Model model) {
        searchAndSetResults(requestForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.REQUEST);
        return new ModelAndView("request", "requestForm", requestForm);
    }

    @ResponseBody
    @RequestMapping(value = "/request", method = RequestMethod.POST, params = "action=next")
    public ModelAndView searchNext(@Valid @ModelAttribute("requestForm") RequestForm requestForm,
                                       BindingResult result,
                                       Model model) {
        searchAndSetResults(requestForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.REQUEST);
        return new ModelAndView("request", "requestForm", requestForm);
    }

    @ResponseBody
    @RequestMapping(value = "/request", method = RequestMethod.POST, params = "action=loadCreateRequest")
    public ModelAndView loadCreateRequest(Model model) {
        RequestForm requestForm = setDefaultsToCreateRequest();
        model.addAttribute("requestForm", requestForm);
        model.addAttribute(RecapConstants.TEMPLATE, RecapConstants.REQUEST);
        return new ModelAndView("request", "requestForm", requestForm);
    }

    private RequestForm setDefaultsToCreateRequest() {
        RequestForm requestForm = new RequestForm();

        List<String> requestingInstitutions = new ArrayList<>();
        List<String> requestTypes = new ArrayList<>();
        List<String> deliveryLocations = new ArrayList<>();

        Iterable<InstitutionEntity> institutionEntities = institutionDetailsRepository.findAll();
        for (Iterator iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
            InstitutionEntity institutionEntity = (InstitutionEntity) iterator.next();
        }

        Iterable<RequestTypeEntity> requestTypeEntities = requestTypeDetailsRepository.findAll();
        for (Iterator iterator = requestTypeEntities.iterator(); iterator.hasNext(); ) {
            RequestTypeEntity requestTypeEntity = (RequestTypeEntity) iterator.next();
            requestTypes.add(requestTypeEntity.getRequestTypeCode());
        }

        Iterable<CustomerCodeEntity> customerCodeEntities = customerCodeDetailsRepository.findAll();
        for (Iterator iterator = customerCodeEntities.iterator(); iterator.hasNext(); ) {
            CustomerCodeEntity customerCodeEntity = (CustomerCodeEntity) iterator.next();
            deliveryLocations.add(customerCodeEntity.getDescription());
        }

        requestForm.setRequestingInstitutions(requestingInstitutions);
        requestForm.setRequestTypes(requestTypes);
        requestForm.setDeliveryLocations(deliveryLocations);
        requestForm.setRequestType("RETRIEVAL");
        return requestForm;
    }

    @ResponseBody
    @RequestMapping(value = "/request", method = RequestMethod.POST, params = "action=populateItem")
    public String populateItem(@Valid @ModelAttribute("requestForm") RequestForm requestForm,
                               BindingResult result,
                               Model model) throws Exception {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isNotBlank(requestForm.getItemBarcodeInRequest())) {
            List<String> itemBarcodes = Arrays.asList(requestForm.getItemBarcodeInRequest().split(","));
            List<String> invalidBarcodes = new ArrayList<>();
            Set<String> itemTitles = new HashSet<>();
            Set<String> itemOwningInstitutions = new HashSet<>();
            for (String itemBarcode : itemBarcodes) {
                String barcode = itemBarcode.trim();
                if (StringUtils.isNotBlank(barcode)) {
                    ItemEntity itemEntity = itemDetailsRepository.findByBarcode(barcode);
                    if (null != itemEntity) {
                        if (CollectionUtils.isNotEmpty(itemEntity.getBibliographicEntities())) {
                            for (BibliographicEntity bibliographicEntity : itemEntity.getBibliographicEntities()) {
                                String bibContent = new String(bibliographicEntity.getContent());
                                BibJSONUtil bibJSONUtil = new BibJSONUtil();
                                List<Record> records = bibJSONUtil.convertMarcXmlToRecord(bibContent);
                                Record marcRecord = records.get(0);
                                itemTitles.add(bibJSONUtil.getTitle(marcRecord));
                                itemOwningInstitutions.add(itemEntity.getInstitutionEntity().getInstitutionCode());
                            }
                        }
                    } else {
                        invalidBarcodes.add(barcode);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(itemTitles)) {
                jsonObject.put("itemTitle", StringUtils.join(itemTitles, " || "));
            }
            if (CollectionUtils.isNotEmpty(itemOwningInstitutions)) {
                jsonObject.put("itemOwningInstitution", StringUtils.join(itemOwningInstitutions, ","));
            }
            if (CollectionUtils.isNotEmpty(invalidBarcodes)) {
                jsonObject.put("errorMessage", RecapConstants.BARCODES_NOT_FOUND + " - " + StringUtils.join(invalidBarcodes, ","));
            }
        }
        return jsonObject.toString();
    }

    private void searchAndSetResults(RequestForm requestForm) {
        Page<RequestItemEntity> requestItemEntities = requestServiceUtil.searchRequests(requestForm);
        List<SearchResultRow> searchResultRows = buildSearchResultRows(requestItemEntities.getContent());
        if (CollectionUtils.isNotEmpty(searchResultRows)) {
            requestForm.setSearchResultRows(searchResultRows);
            requestForm.setTotalRecordsCount(NumberFormat.getNumberInstance().format(requestItemEntities.getTotalElements()));
            requestForm.setTotalPageCount(requestItemEntities.getTotalPages());
        } else {
            requestForm.setSearchResultRows(Collections.EMPTY_LIST);
            requestForm.setMessage(RecapConstants.SEARCH_RESULT_ERROR_NO_RECORDS_FOUND);
        }
        requestForm.setShowResults(true);
    }

    private List<SearchResultRow> buildSearchResultRows(List<RequestItemEntity> requestItemEntities) {
        if (CollectionUtils.isNotEmpty(requestItemEntities)) {
            List<SearchResultRow> searchResultRows = new ArrayList<>();
            for (RequestItemEntity requestItemEntity : requestItemEntities) {
                SearchResultRow searchResultRow = new SearchResultRow();
                searchResultRow.setPatronBarcode(Integer.parseInt(requestItemEntity.getPatronEntity().getInstitutionIdentifier()));
                searchResultRow.setRequestingInstitution(requestItemEntity.getInstitutionEntity().getInstitutionCode());
                searchResultRow.setBarcode(requestItemEntity.getItemEntity().getBarcode());
                searchResultRow.setOwningInstitution(requestItemEntity.getItemEntity().getInstitutionEntity().getInstitutionCode());
                searchResultRow.setDeliveryLocation(requestItemEntity.getStopCode());
                searchResultRow.setRequestType(requestItemEntity.getRequestTypeEntity().getRequestTypeCode());
                if (CollectionUtils.isNotEmpty(requestItemEntity.getNotesEntities())) {
                    searchResultRow.setRequestNotes(requestItemEntity.getNotesEntities().get(0).getNotes());
                }
                ItemEntity itemEntity = requestItemEntity.getItemEntity();
                if (null != itemEntity) {
                    if (CollectionUtils.isNotEmpty(itemEntity.getBibliographicEntities())) {
                        searchResultRow.setBibId(itemEntity.getBibliographicEntities().get(0).getBibliographicId());
                    }
                }
                searchResultRows.add(searchResultRow);
            }
            return searchResultRows;
        }
        return Collections.EMPTY_LIST;
    }
}
