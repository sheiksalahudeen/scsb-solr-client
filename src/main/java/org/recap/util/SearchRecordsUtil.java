package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapConstants;
import org.recap.model.search.SearchItemResultRow;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchResultRow;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Holdings;
import org.recap.model.solr.Item;
import org.recap.repository.solr.main.BibSolrDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by sudhish on 14/10/16.
 */
@Service
public final class SearchRecordsUtil {

    @Autowired
    private BibSolrDocumentRepository bibSolrDocumentRepository;

    public List<SearchResultRow> searchRecords(SearchRecordsRequest searchRecordsRequest) {

        if (!isEmptySearch(searchRecordsRequest)) {
            return searchAndBuildResults(searchRecordsRequest);
        }
        searchRecordsRequest.setErrorMessage(RecapConstants.EMPTY_FACET_ERROR_MSG);
        return new ArrayList<>();
    }

    public List<SearchResultRow> searchAndBuildResults(SearchRecordsRequest searchRecordsRequest) {
        Map<String, Object> searchResponse = bibSolrDocumentRepository.search(searchRecordsRequest);
        String errorResponse = (String) searchResponse.get(RecapConstants.SEARCH_ERROR_RESPONSE);
        if(errorResponse != null) {
            searchRecordsRequest.setErrorMessage(RecapConstants.SERVER_ERROR_MSG);
        } else {
            List<BibItem> bibItems = (List<BibItem>) searchResponse.get(RecapConstants.SEARCH_SUCCESS_RESPONSE);
            return buildResults(bibItems);
        }

        return new ArrayList<>();
    }

    private List<SearchResultRow> buildResults(List<BibItem> bibItems) {
        List<SearchResultRow> searchResultRows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bibItems)) {
            for (BibItem bibItem : bibItems) {
                SearchResultRow searchResultRow = new SearchResultRow();
                searchResultRow.setBibId(bibItem.getBibId());
                searchResultRow.setTitle(bibItem.getTitleDisplay());
                searchResultRow.setAuthor(bibItem.getAuthorDisplay());
                searchResultRow.setPublisher(bibItem.getPublisher());
                searchResultRow.setPublisherDate(bibItem.getPublicationDate());
                searchResultRow.setOwningInstitution(bibItem.getOwningInstitution());
                searchResultRow.setLeaderMaterialType(bibItem.getLeaderMaterialType());
                Holdings holdings = CollectionUtils.isEmpty(bibItem.getHoldingsList()) ? new Holdings() : bibItem.getHoldingsList().get(0);
                if (null != bibItem.getItems() && bibItem.getItems().size() == 1 && !RecapConstants.SERIAL.equals(bibItem.getLeaderMaterialType())) {
                    Item item = bibItem.getItems().get(0);
                    if (null != item) {
                        searchResultRow.setItemId(item.getItemId());
                        searchResultRow.setCustomerCode(item.getCustomerCode());
                        searchResultRow.setCollectionGroupDesignation(item.getCollectionGroupDesignation());
                        searchResultRow.setUseRestriction(item.getUseRestrictionDisplay());
                        searchResultRow.setBarcode(item.getBarcode());
                        searchResultRow.setAvailability(item.getAvailabilityDisplay());
                        searchResultRow.setSummaryHoldings(holdings.getSummaryHoldings());
                    }
                } else {
                    if (!CollectionUtils.isEmpty(bibItem.getItems())) {
                        List<SearchItemResultRow> searchItemResultRows = new ArrayList<>();
                        for (Item item : bibItem.getItems()) {
                            if (null != item) {
                                SearchItemResultRow searchItemResultRow = new SearchItemResultRow();
                                searchItemResultRow.setCallNumber(item.getCallNumberDisplay());
                                searchItemResultRow.setChronologyAndEnum(item.getVolumePartYear());
                                searchItemResultRow.setCustomerCode(item.getCustomerCode());
                                searchItemResultRow.setBarcode(item.getBarcode());
                                searchItemResultRow.setUseRestriction(item.getUseRestrictionDisplay());
                                searchItemResultRow.setCollectionGroupDesignation(item.getCollectionGroupDesignation());
                                searchItemResultRow.setAvailability(item.getAvailabilityDisplay());
                                searchItemResultRows.add(searchItemResultRow);
                            }
                        }
                        searchResultRow.setSummaryHoldings(holdings.getSummaryHoldings());
                        searchResultRow.setShowItems(true);
                        Collections.sort(searchItemResultRows);
                        searchResultRow.setSearchItemResultRows(searchItemResultRows);
                    }
                }
                searchResultRows.add(searchResultRow);
            }
        }
        return searchResultRows;
    }

    private boolean isEmptySearch(SearchRecordsRequest searchRecordsRequest) {
        boolean emptySearch = false;
        if (searchRecordsRequest.getMaterialTypes().size() == 0 && searchRecordsRequest.getAvailability().size() == 0 &&
                searchRecordsRequest.getCollectionGroupDesignations().size() == 0 && searchRecordsRequest.getOwningInstitutions().size() == 0 && searchRecordsRequest.getUseRestrictions().size() == 0) {
            emptySearch = true;
        } else if(!((CollectionUtils.isNotEmpty(searchRecordsRequest.getMaterialTypes()) || CollectionUtils.isNotEmpty(searchRecordsRequest.getOwningInstitutions())) &&
                (CollectionUtils.isNotEmpty(searchRecordsRequest.getAvailability()) || CollectionUtils.isNotEmpty(searchRecordsRequest.getCollectionGroupDesignations())
                        || CollectionUtils.isNotEmpty(searchRecordsRequest.getUseRestrictions())))) {
            emptySearch = true;
        }
        return emptySearch;
    }
}
