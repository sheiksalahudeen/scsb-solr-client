package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.model.search.SearchItemResultRow;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchResultRow;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Item;
import org.recap.repository.solr.main.BibSolrDocumentRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rajeshbabuk on 6/7/16.
 */

@Controller
public class SearchRecordsController {

    Logger logger = LoggerFactory.getLogger(SearchRecordsController.class);

    @Autowired
    BibSolrDocumentRepository bibSolrDocumentRepository;

    @Autowired
    ItemCrudRepository itemCrudRepository;

    @RequestMapping("/search")
    public String searchRecords(Model model) {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        setDefaultsToSearchRecordsRequest(searchRecordsRequest);
        model.addAttribute("searchRecordsRequest", searchRecordsRequest);
        return "searchRecords";
    }

    @ResponseBody
    @RequestMapping(value = "/searchRecords", method = RequestMethod.POST, params = "action=search")
    public ModelAndView search(@Valid @ModelAttribute("searchRecordsRequest") SearchRecordsRequest searchRecordsRequest,
                                  BindingResult result,
                                  Model model) {
        if(!isEmptySearch(searchRecordsRequest)){
            List<BibItem> bibItems = bibSolrDocumentRepository.search(searchRecordsRequest, new PageRequest(0, searchRecordsRequest.getPageSize()));
            buildResults(searchRecordsRequest, bibItems);
            return new ModelAndView("searchRecords", "searchRecordsRequest", searchRecordsRequest);
        }
        searchRecordsRequest.setErrorMessage("At least one Search Facet Box needs to be checked to get results.");
        return new ModelAndView("searchRecords", "searchRecordsRequest", searchRecordsRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/searchRecords", method = RequestMethod.POST, params = "action=previous")
    public ModelAndView searchPrevious(@Valid @ModelAttribute("searchRecordsRequest") SearchRecordsRequest searchRecordsRequest,
                               BindingResult result,
                               Model model) {
        searchRecordsRequest.setSearchResultRows(null);
        searchAndBuildResults(searchRecordsRequest);
        return new ModelAndView("searchRecords", "searchRecordsRequest", searchRecordsRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/searchRecords", method = RequestMethod.POST, params = "action=next")
    public ModelAndView searchNext(@Valid @ModelAttribute("searchRecordsRequest") SearchRecordsRequest searchRecordsRequest,
                                   BindingResult result,
                                   Model model) {
        searchRecordsRequest.setSearchResultRows(null);
        searchAndBuildResults(searchRecordsRequest);
        return new ModelAndView("searchRecords", "searchRecordsRequest", searchRecordsRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/searchRecords", method = RequestMethod.POST, params = "action=first")
    public ModelAndView searchFirst(@Valid @ModelAttribute("searchRecordsRequest") SearchRecordsRequest searchRecordsRequest,
                                       BindingResult result,
                                       Model model) {
        searchRecordsRequest.setSearchResultRows(null);
        List<BibItem> bibItems = bibSolrDocumentRepository.search(searchRecordsRequest, new PageRequest(0, searchRecordsRequest.getPageSize()));
        buildResults(searchRecordsRequest, bibItems);
        return new ModelAndView("searchRecords", "searchRecordsRequest", searchRecordsRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/searchRecords", method = RequestMethod.POST, params = "action=last")
    public ModelAndView searchLast(@Valid @ModelAttribute("searchRecordsRequest") SearchRecordsRequest searchRecordsRequest,
                                       BindingResult result,
                                       Model model) {
        searchRecordsRequest.setSearchResultRows(null);
        List<BibItem> bibItems = bibSolrDocumentRepository.search(searchRecordsRequest, new PageRequest(searchRecordsRequest.getTotalPageCount() - 1, searchRecordsRequest.getPageSize()));
        buildResults(searchRecordsRequest, bibItems);
        return new ModelAndView("searchRecords", "searchRecordsRequest", searchRecordsRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/searchRecords", method = RequestMethod.POST, params = "action=clear")
    public ModelAndView clear(@Valid @ModelAttribute("searchRecordsRequest") SearchRecordsRequest searchRecordsRequest,
                              BindingResult result,
                              Model model) {

        searchRecordsRequest.setFieldValue("");
        searchRecordsRequest.setOwningInstitutions(new ArrayList<>());
        searchRecordsRequest.setCollectionGroupDesignations(new ArrayList<>());
        searchRecordsRequest.setAvailability(new ArrayList<>());
        searchRecordsRequest.setMaterialTypes(new ArrayList<>());
        searchRecordsRequest.setShowResults(false);
        return new ModelAndView("searchRecords");
    }

    @ResponseBody
    @RequestMapping(value = "/searchRecords", method = RequestMethod.POST, params = "action=newSearch")
    public ModelAndView newSearch(@Valid @ModelAttribute("searchRecordsRequest") SearchRecordsRequest searchRecordsRequest,
                                  BindingResult result,
                                  Model model) {
        setDefaultsToSearchRecordsRequest(searchRecordsRequest);
        return new ModelAndView("searchRecords");
    }

    @ResponseBody
    @RequestMapping(value = "/searchRecords", method = RequestMethod.POST, params = "action=request")
    public ModelAndView requestRecords(@Valid @ModelAttribute("searchRecordsRequest") SearchRecordsRequest searchRecordsRequest,
                                  BindingResult result,
                                  Model model) {
        return new ModelAndView("searchRecords");
    }

    @ResponseBody
    @RequestMapping(value = "/searchRecords", method = RequestMethod.POST, params = "action=export")
    public ModelAndView exportRecords(@Valid @ModelAttribute("searchRecordsRequest") SearchRecordsRequest searchRecordsRequest,
                                  BindingResult result,
                                  Model model) {
        return new ModelAndView("searchRecords");
    }

    @ResponseBody
    @RequestMapping(value = "/searchRecords", method = RequestMethod.POST, params = "action=pageSizeChange")
    public ModelAndView onPageSizeChange(@Valid @ModelAttribute("searchRecordsRequest") SearchRecordsRequest searchRecordsRequest,
                                         BindingResult result,
                                         Model model) throws Exception {
        int totalRecordsCount = NumberFormat.getNumberInstance().parse(searchRecordsRequest.getTotalRecordsCount()).intValue();
        int totalPagesCount = (int) Math.ceil((double) totalRecordsCount / (double) searchRecordsRequest.getPageSize());
        Integer pageNumber = searchRecordsRequest.getPageNumber();
        if (totalPagesCount > 0 && pageNumber >= totalPagesCount) {
            pageNumber = totalPagesCount - 1;
        }
        List<BibItem> bibItems = bibSolrDocumentRepository.search(searchRecordsRequest, new PageRequest(pageNumber, searchRecordsRequest.getPageSize()));
        buildResults(searchRecordsRequest, bibItems);
        return new ModelAndView("searchRecords", "searchRecordsRequest", searchRecordsRequest);
    }

    private void setDefaultsToSearchRecordsRequest(SearchRecordsRequest searchRecordsRequest) {
        searchRecordsRequest.setFieldName("");
        searchRecordsRequest.setFieldValue("");

        searchRecordsRequest.getOwningInstitutions().add("NYPL");
        searchRecordsRequest.getOwningInstitutions().add("CUL");
        searchRecordsRequest.getOwningInstitutions().add("PUL");

        searchRecordsRequest.getCollectionGroupDesignations().add("Shared");
        searchRecordsRequest.getCollectionGroupDesignations().add("Private");
        searchRecordsRequest.getCollectionGroupDesignations().add("Open");

        searchRecordsRequest.getAvailability().add("Available");
        searchRecordsRequest.getAvailability().add("Not Available");

        searchRecordsRequest.getMaterialTypes().add("Monograph");
        searchRecordsRequest.getMaterialTypes().add("Serial");
        searchRecordsRequest.getMaterialTypes().add("Other");

        searchRecordsRequest.setPageNumber(0);
        searchRecordsRequest.setPageSize(10);
        searchRecordsRequest.setShowResults(false);
    }

    private void searchAndBuildResults(SearchRecordsRequest searchRecordsRequest) {
        List<BibItem> bibItems = bibSolrDocumentRepository.search(searchRecordsRequest, new PageRequest(searchRecordsRequest.getPageNumber(), searchRecordsRequest.getPageSize()));
        buildResults(searchRecordsRequest, bibItems);
    }

    private void buildResults(SearchRecordsRequest searchRecordsRequest, List<BibItem> bibItems) {
        searchRecordsRequest.setSearchResultRows(null);
        searchRecordsRequest.setShowResults(true);
        searchRecordsRequest.setSelectAll(false);
        if (!CollectionUtils.isEmpty(bibItems)) {
            List<SearchResultRow> searchResultRows = new ArrayList<>();
            for (BibItem bibItem : bibItems) {
                SearchResultRow searchResultRow = new SearchResultRow();
                searchResultRow.setBibId(bibItem.getBibId());
                searchResultRow.setTitle(bibItem.getTitleDisplay());
                searchResultRow.setAuthor(bibItem.getAuthorDisplay());
                searchResultRow.setPublisher(bibItem.getPublisher());
                searchResultRow.setPublisherDate(bibItem.getPublicationDate());
                searchResultRow.setOwningInstitution(bibItem.getOwningInstitution());
                searchResultRow.setLeaderMaterialType(bibItem.getLeaderMaterialType());
                if (null != bibItem.getItems() && bibItem.getItems().size() == 1 && !RecapConstants.SERIAL.equals(bibItem.getLeaderMaterialType())) {
                    Item item = bibItem.getItems().get(0);
                    searchResultRow.setCustomerCode(item.getCustomerCode());
                    searchResultRow.setCollectionGroupDesignation(item.getCollectionGroupDesignation());
                    searchResultRow.setUseRestriction(item.getUseRestriction());
                    searchResultRow.setBarcode(item.getBarcode());
                    searchResultRow.setAvailability(item.getAvailability());
                    searchResultRow.setSummaryHoldings(item.getSummaryHoldings());
                } else {
                    if (!CollectionUtils.isEmpty(bibItem.getItems())) {
                        List<SearchItemResultRow> searchItemResultRows = new ArrayList<>();
                        for (Item item : bibItem.getItems()) {
                            SearchItemResultRow searchItemResultRow = new SearchItemResultRow();
                            searchItemResultRow.setCallNumber(item.getCallNumber());
                            searchItemResultRow.setChronologyAndEnum(item.getVolumePartYear());
                            searchItemResultRow.setCustomerCode(item.getCustomerCode());
                            searchItemResultRow.setBarcode(item.getBarcode());
                            searchItemResultRow.setUseRestriction(item.getUseRestriction());
                            searchItemResultRow.setCollectionGroupDesignation(item.getCollectionGroupDesignation());
                            searchItemResultRow.setAvailability(item.getAvailability());
                            searchItemResultRows.add(searchItemResultRow);
                        }
                        searchResultRow.setSummaryHoldings(bibItem.getItems().get(0).getSummaryHoldings());
                        searchResultRow.setShowItems(true);
                        Collections.sort(searchItemResultRows);
                        searchResultRow.setSearchItemResultRows(searchItemResultRows);
                    }
                }
                searchResultRows.add(searchResultRow);
            }
            searchRecordsRequest.setSearchResultRows(searchResultRows);
        }
    }

    private boolean isEmptySearch(SearchRecordsRequest searchRecordsRequest) {
        boolean emptySearch = false;
        if (searchRecordsRequest.getMaterialTypes().size() == 0 && searchRecordsRequest.getAvailability().size() == 0 &&
                searchRecordsRequest.getCollectionGroupDesignations().size() == 0 && searchRecordsRequest.getOwningInstitutions().size() == 0) {
            emptySearch = true;
        }
        return emptySearch;
    }
}
