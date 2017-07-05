package org.recap.repository.solr.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.search.resolver.HoldingsValueResolver;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.search.resolver.impl.bib.*;
import org.recap.model.search.resolver.impl.bib.DocTypeValueResolver;
import org.recap.model.search.resolver.impl.bib.IdValueResolver;
import org.recap.model.search.resolver.impl.holdings.*;
import org.recap.model.search.resolver.impl.item.*;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Holdings;
import org.recap.model.solr.Item;
import org.recap.repository.solr.main.CustomDocumentRepository;
import org.recap.util.SolrQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by rajeshbabuk on 8/7/16.
 */
@Repository
public class BibSolrDocumentRepositoryImpl implements CustomDocumentRepository {

    private static final Logger logger = LoggerFactory.getLogger(BibSolrDocumentRepositoryImpl.class);

    @Resource
    private SolrTemplate solrTemplate;

    @Autowired
    private SolrQueryBuilder solrQueryBuilder;

    private List<BibValueResolver> bibValueResolvers;

    private List<ItemValueResolver> itemValueResolvers;

    private List<HoldingsValueResolver> holdingsValueResolvers;

    @Override
    public Map<String,Object> search(SearchRecordsRequest searchRecordsRequest) {
        List<BibItem> bibItems = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        try {
            if (isEmptyField(searchRecordsRequest)) {
                searchRecordsRequest.setShowTotalCount(true);
                searchRecordsRequest.setFieldName(RecapConstants.ALL_FIELDS);
                bibItems = searchByBib(searchRecordsRequest);
                if(CollectionUtils.isEmpty(bibItems)) {
                    bibItems = searchByItem(searchRecordsRequest);
                }
                searchRecordsRequest.setFieldName("");
            } else if (isItemField(searchRecordsRequest)) {
                bibItems = searchByItem(searchRecordsRequest);
            } else {
                bibItems = searchByBib(searchRecordsRequest);
            }
            response.put(RecapConstants.SEARCH_SUCCESS_RESPONSE, bibItems);
        } catch (IOException|SolrServerException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            response.put(RecapConstants.SEARCH_ERROR_RESPONSE, e.getMessage());
        }
        return response;
    }

    private boolean isEmptyField(SearchRecordsRequest searchRecordsRequest) {
        if (StringUtils.isBlank(searchRecordsRequest.getFieldName()) && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())) {
            return true;
        }
        return false;
    }

    private List<BibItem> searchByItem(SearchRecordsRequest searchRecordsRequest) throws SolrServerException, IOException {
        List<BibItem> bibItems = new ArrayList<>();
        SolrQuery queryForChildAndParentCriteria = solrQueryBuilder.getQueryForChildAndParentCriteria(searchRecordsRequest);
        queryForChildAndParentCriteria.setStart(searchRecordsRequest.getPageNumber() * searchRecordsRequest.getPageSize());
        queryForChildAndParentCriteria.setRows(searchRecordsRequest.getPageSize());
        if (searchRecordsRequest.isSortIncompleteRecords()){
            queryForChildAndParentCriteria.setSort(RecapConstants.ITEM_CREATED_DATE, SolrQuery.ORDER.desc);
        }
        else {
            queryForChildAndParentCriteria.setSort(RecapConstants.TITLE_SORT, SolrQuery.ORDER.asc);
        }
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(queryForChildAndParentCriteria);
        SolrDocumentList itemSolrDocumentList = queryResponse.getResults();
        if (CollectionUtils.isNotEmpty(itemSolrDocumentList)) {
            setCountsByItem(searchRecordsRequest, itemSolrDocumentList);
            for (Iterator<SolrDocument> iterator = itemSolrDocumentList.iterator(); iterator.hasNext(); ) {
                SolrDocument itemSolrDocument = iterator.next();
                Item item = getItem(itemSolrDocument);
                bibItems.addAll(getBibItemsAndHoldings(item, searchRecordsRequest.isDeleted(), searchRecordsRequest.getCatalogingStatus()));
            }
        }
        return bibItems;
    }

    private List<BibItem> searchByBib(SearchRecordsRequest searchRecordsRequest) throws SolrServerException, IOException {
        List<BibItem> bibItems = new ArrayList<>();
        SolrQuery queryForParentAndChildCriteria = solrQueryBuilder.getQueryForParentAndChildCriteria(searchRecordsRequest);
        queryForParentAndChildCriteria.setStart(searchRecordsRequest.getPageNumber() * searchRecordsRequest.getPageSize());
        queryForParentAndChildCriteria.setRows(searchRecordsRequest.getPageSize());
        queryForParentAndChildCriteria.setSort(RecapConstants.TITLE_SORT, SolrQuery.ORDER.asc);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(queryForParentAndChildCriteria);
        SolrDocumentList bibSolrDocumentList = queryResponse.getResults();
        if(CollectionUtils.isNotEmpty(bibSolrDocumentList)) {
            setCountsByBib(searchRecordsRequest, bibSolrDocumentList);
            for (Iterator<SolrDocument> iterator = bibSolrDocumentList.iterator(); iterator.hasNext(); ) {
                SolrDocument bibSolrDocument = iterator.next();
                BibItem bibItem = new BibItem();
                populateBibItem(bibSolrDocument, bibItem);
                populateItemHoldingsInfo(bibItem, searchRecordsRequest.isDeleted(), searchRecordsRequest.getCatalogingStatus());
                bibItems.add(bibItem);
            }
        }
        return bibItems;
    }

    private List<BibItem> getBibItemsAndHoldings(Item item, boolean isDeleted, String catalogingStatus) {
        List<BibItem> bibItems = new ArrayList<>();
        SolrQuery solrQueryForBib = solrQueryBuilder.getSolrQueryForBibItem("_root_:" + item.getRoot());
        QueryResponse queryResponse = null;
        try {
            queryResponse = solrTemplate.getSolrClient().query(solrQueryForBib);
            SolrDocumentList solrDocuments = queryResponse.getResults();
            if(solrDocuments.getNumFound() > 10 ) {
                solrQueryForBib.setRows((int) solrDocuments.getNumFound());
                queryResponse = solrTemplate.getSolrClient().query(solrQueryForBib);
                solrDocuments = queryResponse.getResults();
            }
            BibItem bibItem = new BibItem();
            for (Iterator<SolrDocument> iterator = solrDocuments.iterator(); iterator.hasNext(); ) {
                SolrDocument solrDocument = iterator.next();
                String docType = (String) solrDocument.getFieldValue(RecapConstants.DOCTYPE);
                if (docType.equalsIgnoreCase(RecapConstants.BIB)) {
                    boolean isDeletedBib = (boolean) solrDocument.getFieldValue(RecapConstants.IS_DELETED_BIB);
                    String bibCatalogingStatus = (String) solrDocument.getFieldValue(RecapConstants.BIB_CATALOGING_STATUS);
                    if (isDeletedBib == isDeleted && catalogingStatus.equals(bibCatalogingStatus)) {
                        populateBibItem(solrDocument, bibItem);
                        bibItem.setItems(Arrays.asList(item));
                    }
                }
                if(docType.equalsIgnoreCase(RecapConstants.HOLDINGS)) {
                    boolean isDeletedHoldings = (boolean) solrDocument.getFieldValue(RecapConstants.IS_DELETED_HOLDINGS);
                    if (isDeletedHoldings == isDeleted) {
                        Holdings holdings = getHoldings(solrDocument);
                        bibItem.addHoldings(holdings);
                    }
                }
            }
            bibItems.add(bibItem);
        } catch (IOException|SolrServerException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return bibItems;
    }

    /**
     * Populate item holdings info based on isdeleted flag and item cataloging status.
     *
     * @param bibItem          the bib item
     * @param isDeleted        the is deleted
     * @param catalogingStatus the cataloging status
     */
    public void populateItemHoldingsInfo(BibItem bibItem, boolean isDeleted, String catalogingStatus) {
        SolrQuery solrQueryForItem = solrQueryBuilder.getSolrQueryForBibItem("_root_:" + bibItem.getRoot());
        QueryResponse queryResponse = null;
        try {
            queryResponse = solrTemplate.getSolrClient().query(solrQueryForItem);
            SolrDocumentList solrDocuments = queryResponse.getResults();
            if(solrDocuments.getNumFound() > 10 ) {
                solrQueryForItem.setRows((int) solrDocuments.getNumFound());
                queryResponse = solrTemplate.getSolrClient().query(solrQueryForItem);
                solrDocuments = queryResponse.getResults();
            }
            for (Iterator<SolrDocument> iterator = solrDocuments.iterator(); iterator.hasNext(); ) {
                SolrDocument solrDocument = iterator.next();
                String docType = (String) solrDocument.getFieldValue(RecapConstants.DOCTYPE);
                if(docType.equalsIgnoreCase(RecapConstants.ITEM)) {
                    boolean isDeletedItem = (boolean) solrDocument.getFieldValue(RecapConstants.IS_DELETED_ITEM);
                    String itemCatalogingStatus = (String) solrDocument.getFieldValue(RecapConstants.ITEM_CATALOGING_STATUS);
                    if (isDeletedItem == isDeleted && catalogingStatus.equals(itemCatalogingStatus)) {
                        Item item = getItem(solrDocument);
                        bibItem.addItem(item);
                    }
                }
                if(docType.equalsIgnoreCase(RecapConstants.HOLDINGS)) {
                    boolean isDeletedHoldings = (boolean) solrDocument.getFieldValue(RecapConstants.IS_DELETED_HOLDINGS);
                    if (isDeletedHoldings == isDeleted) {
                        Holdings holdings = getHoldings(solrDocument);
                        bibItem.addHoldings(holdings);
                    }
                }
            }
        } catch (IOException|SolrServerException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
    }

    private boolean isItemField(SearchRecordsRequest searchRecordsRequest) {
        if (StringUtils.isNotBlank(searchRecordsRequest.getFieldName())
                && (searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.BARCODE)
                || searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.CALL_NUMBER)
                || searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.ITEM_CATALOGING_STATUS)
                || searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.CUSTOMER_CODE))) {
            return true;
        }
        return false;
    }

    private void setCountsByBib(SearchRecordsRequest searchRecordsRequest, SolrDocumentList bibSolrDocuments) throws IOException, SolrServerException {
        long numFound = bibSolrDocuments.getNumFound();
        String totalBibCount = NumberFormat.getNumberInstance().format(numFound);
        searchRecordsRequest.setTotalBibRecordsCount(totalBibCount);
        searchRecordsRequest.setTotalRecordsCount(totalBibCount);
        if(!searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.ALL_FIELDS)) {
            String totalItemCount = NumberFormat.getNumberInstance().format(getItemCountsForBib(searchRecordsRequest));
            searchRecordsRequest.setTotalItemRecordsCount(totalItemCount);
        }
        int totalPagesCount = (int) Math.ceil((double) numFound / (double) searchRecordsRequest.getPageSize());
        searchRecordsRequest.setTotalPageCount(totalPagesCount);
    }

    private long getItemCountsForBib(SearchRecordsRequest searchRecordsRequest) throws IOException, SolrServerException {
        SolrQuery queryForChildAndParentCriteria = solrQueryBuilder.getCountQueryForChildAndParentCriteria(searchRecordsRequest);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(queryForChildAndParentCriteria);
        return queryResponse.getResults().getNumFound();
    }

    private void setCountsByItem(SearchRecordsRequest searchRecordsRequest, SolrDocumentList itemSolrDocuments) throws IOException, SolrServerException {
        long numFound = itemSolrDocuments.getNumFound();
        String totalItemCount = NumberFormat.getNumberInstance().format(numFound);
        searchRecordsRequest.setTotalItemRecordsCount(totalItemCount);
        searchRecordsRequest.setTotalRecordsCount(totalItemCount);
        if(!searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.ALL_FIELDS)) {
            String totalBibCount = NumberFormat.getNumberInstance().format(getBibCountsForItem(searchRecordsRequest));
            searchRecordsRequest.setTotalBibRecordsCount(totalBibCount);
        }
        int totalPagesCount = (int) Math.ceil((double) numFound / (double) searchRecordsRequest.getPageSize());
        searchRecordsRequest.setTotalPageCount(totalPagesCount);
    }

    private long getBibCountsForItem(SearchRecordsRequest searchRecordsRequest) throws IOException, SolrServerException {
        SolrQuery queryForParentAndChildCriteria = solrQueryBuilder.getCountQueryForParentAndChildCriteria(searchRecordsRequest);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(queryForParentAndChildCriteria);
        return queryResponse.getResults().getNumFound();
    }

    /**
     * Gets item for the give item solr document.
     *
     * @param itemSolrDocument the item solr document
     * @return the item
     */
    public Item getItem(SolrDocument itemSolrDocument) {
        Item item = new Item();
        Collection<String> fieldNames = itemSolrDocument.getFieldNames();
        List<ItemValueResolver> itemValueResolvers = getItemValueResolvers();
        for (Iterator<String> iterator = fieldNames.iterator(); iterator.hasNext(); ) {
            String fieldName = iterator.next();
            Object fieldValue = itemSolrDocument.getFieldValue(fieldName);
            for (Iterator<ItemValueResolver> itemValueResolverIterator = itemValueResolvers.iterator(); itemValueResolverIterator.hasNext(); ) {
                ItemValueResolver itemValueResolver = itemValueResolverIterator.next();
                if (itemValueResolver.isInterested(fieldName)) {
                    itemValueResolver.setValue(item, fieldValue);
                }
            }
        }
        return item;
    }

    /**
     * Gets holdings for the give holdings solr document.
     *
     * @param holdingsSolrDocument the holdings solr document
     * @return the holdings
     */
    public Holdings getHoldings(SolrDocument holdingsSolrDocument) {
        Holdings holdings = new Holdings();
        Collection<String> fieldNames = holdingsSolrDocument.getFieldNames();
        List<HoldingsValueResolver> holdingsValueResolvers = getHoldingsValueResolvers();
        for (Iterator<String> iterator = fieldNames.iterator(); iterator.hasNext(); ) {
            String fieldName = iterator.next();
            Object fieldValue = holdingsSolrDocument.getFieldValue(fieldName);
            for (Iterator<HoldingsValueResolver> holdingsValueResolverIterator = holdingsValueResolvers.iterator(); holdingsValueResolverIterator.hasNext(); ) {
                HoldingsValueResolver holdingsValueResolver = holdingsValueResolverIterator.next();
                if(holdingsValueResolver.isInterested(fieldName)) {
                    holdingsValueResolver.setValue(holdings, fieldValue);
                }
            }
        }
        return holdings;
    }

    /**
     * Populate bib item based on input solr document.
     *
     * @param solrDocument the solr document
     * @param bibItem      the bib item
     */
    public void populateBibItem(SolrDocument solrDocument, BibItem bibItem) {
        Collection<String> fieldNames = solrDocument.getFieldNames();
        for (Iterator<String> stringIterator = fieldNames.iterator(); stringIterator.hasNext(); ) {
            String fieldName = stringIterator.next();
            Object fieldValue = solrDocument.getFieldValue(fieldName);
            for (Iterator<BibValueResolver> valueResolverIterator = getBibValueResolvers().iterator(); valueResolverIterator.hasNext(); ) {
                BibValueResolver valueResolver = valueResolverIterator.next();
                if (valueResolver.isInterested(fieldName)) {
                    valueResolver.setValue(bibItem, fieldValue);
                }
            }
        }
    }

    /**
     * Gets list of bib value resolvers which is used to set appropriated values in bib .
     *
     * @return the bib value resolvers
     */
    public List<BibValueResolver> getBibValueResolvers() {
        if (null == bibValueResolvers) {
            bibValueResolvers = new ArrayList<>();
            bibValueResolvers.add(new RootValueResolver());
            bibValueResolvers.add(new AuthorDisplayValueResolver());
            bibValueResolvers.add(new AuthorSearchValueResolver());
            bibValueResolvers.add(new BibIdValueResolver());
            bibValueResolvers.add(new DocTypeValueResolver());
            bibValueResolvers.add(new IdValueResolver());
            bibValueResolvers.add(new ImprintValueResolver());
            bibValueResolvers.add(new ISBNValueResolver());
            bibValueResolvers.add(new ISSNValueResolver());
            bibValueResolvers.add(new LCCNValueResolver());
            bibValueResolvers.add(new LeaderMaterialTypeValueResolver());
            bibValueResolvers.add(new MaterialTypeValueResolver());
            bibValueResolvers.add(new NotesValueResolver());
            bibValueResolvers.add(new OCLCValueResolver());
            bibValueResolvers.add(new OwningInstitutionBibIdValueResolver());
            bibValueResolvers.add(new OwningInstitutionValueResolver());
            bibValueResolvers.add(new PublicationDateValueResolver());
            bibValueResolvers.add(new PublicationPlaceValueResolver());
            bibValueResolvers.add(new PublisherValueResolver());
            bibValueResolvers.add(new SubjectValueResolver());
            bibValueResolvers.add(new TitleDisplayValueResolver());
            bibValueResolvers.add(new TitleSearchValueResolver());
            bibValueResolvers.add(new TitleSortValueResolver());
            bibValueResolvers.add(new IsDeletedBibValueResolver());
            bibValueResolvers.add(new BibCreatedDateValueResolver());
        }
        return bibValueResolvers;
    }

    /**
     * Gets list of item value resolvers which is used to set appropriated values in item.
     *
     * @return the item value resolvers
     */
    public List<ItemValueResolver> getItemValueResolvers() {
        if (null == itemValueResolvers) {
            itemValueResolvers = new ArrayList<>();
            itemValueResolvers.add(new AvailabilitySearchValueResolver());
            itemValueResolvers.add(new AvailabilityDisplayValueResolver());
            itemValueResolvers.add(new BarcodeValueResolver());
            itemValueResolvers.add(new CallNumberSearchValueResolver());
            itemValueResolvers.add(new CallNumberDisplayValueResolver());
            itemValueResolvers.add(new CollectionGroupDesignationValueResolver());
            itemValueResolvers.add(new CustomerCodeValueResolver());
            itemValueResolvers.add(new org.recap.model.search.resolver.impl.item.DocTypeValueResolver());
            itemValueResolvers.add(new ItemOwningInstitutionValueResolver());
            itemValueResolvers.add(new UseRestrictionSearchValueResolver());
            itemValueResolvers.add(new UseRestrictionDisplayValueResolver());
            itemValueResolvers.add(new VolumePartYearValueResolver());
            itemValueResolvers.add(new ItemRootValueResolver());
            itemValueResolvers.add(new ItemIdValueResolver());
            itemValueResolvers.add(new org.recap.model.search.resolver.impl.item.IdValueResolver());
            itemValueResolvers.add(new IsDeletedItemValueResolver());
            itemValueResolvers.add(new ItemCreatedDateValueResolver());
            itemValueResolvers.add(new OwningInstitutionItemIdValueResolver());
            itemValueResolvers.add(new HoldingsIdsValueResolver());
        }
        return itemValueResolvers;
    }

    /**
     * Gets list of holdings value resolvers which is used to set appropriated values in holdings.
     *
     * @return the holdings value resolvers
     */
    public List<HoldingsValueResolver> getHoldingsValueResolvers() {
        if(null == holdingsValueResolvers) {
            holdingsValueResolvers = new ArrayList<>();
            holdingsValueResolvers.add(new HoldingsRootValueResolver());
            holdingsValueResolvers.add(new SummaryHoldingsValueResolver());
            holdingsValueResolvers.add(new org.recap.model.search.resolver.impl.holdings.DocTypeValueResolver());
            holdingsValueResolvers.add(new org.recap.model.search.resolver.impl.holdings.IdValueResolver());
            holdingsValueResolvers.add(new HoldingsIdValueResolver());
            holdingsValueResolvers.add(new IsDeletedHoldingsValueResolver());
            holdingsValueResolvers.add(new OwningInstitutionHoldingsIdValueResolver());
        }
        return holdingsValueResolvers;
    }

    @Override
    public Integer getPageNumberOnPageSizeChange(SearchRecordsRequest searchRecordsRequest) {
        int totalRecordsCount;
        Integer pageNumber = searchRecordsRequest.getPageNumber();
        try {
            if (isEmptyField(searchRecordsRequest)) {
                totalRecordsCount = NumberFormat.getNumberInstance().parse(searchRecordsRequest.getTotalRecordsCount()).intValue();
            } else if (isItemField(searchRecordsRequest)) {
                totalRecordsCount = NumberFormat.getNumberInstance().parse(searchRecordsRequest.getTotalItemRecordsCount()).intValue();
            } else {
                totalRecordsCount = NumberFormat.getNumberInstance().parse(searchRecordsRequest.getTotalBibRecordsCount()).intValue();
            }
            int totalPagesCount = (int) Math.ceil((double) totalRecordsCount / (double) searchRecordsRequest.getPageSize());
            if (totalPagesCount > 0 && pageNumber >= totalPagesCount) {
                pageNumber = totalPagesCount - 1;
            }
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return pageNumber;
    }
}
