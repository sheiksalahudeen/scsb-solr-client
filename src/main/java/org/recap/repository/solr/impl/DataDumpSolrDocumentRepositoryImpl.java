package org.recap.repository.solr.impl;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.search.resolver.impl.bib.*;
import org.recap.model.search.resolver.impl.item.IsDeletedItemValueResolver;
import org.recap.model.search.resolver.impl.item.ItemBibIdValueResolver;
import org.recap.model.search.resolver.impl.item.ItemIdValueResolver;
import org.recap.model.search.resolver.impl.item.ItemRootValueResolver;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.solr.main.CustomDocumentRepository;
import org.recap.util.SolrQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * Created by angelind on 26/10/16.
 */
@Repository
public class DataDumpSolrDocumentRepositoryImpl implements CustomDocumentRepository {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpSolrDocumentRepositoryImpl.class);

    private String and = " AND ";

    private String or = " OR ";

    @Resource
    private SolrTemplate solrTemplate;

    @Autowired
    private SolrQueryBuilder solrQueryBuilder;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    private List<BibValueResolver> bibValueResolvers;

    private List<ItemValueResolver> itemValueResolvers;

    @Value("${datadump.deleted.type.onlyorphan.institution}")
    private String deletedOnlyOrphanInstitution;

    @Override
    public Map<String, Object> search(SearchRecordsRequest searchRecordsRequest) {
        List<BibItem> bibItems;
        Map<String, Object> response = new HashMap<>();
        try {
            searchRecordsRequest.setShowTotalCount(true);
            searchRecordsRequest.setFieldName(StringUtils.isEmpty(searchRecordsRequest.getFieldName()) ? RecapConstants.ALL_FIELDS : searchRecordsRequest.getFieldName());
            if(searchRecordsRequest.isDeleted()) {
                bibItems = searchByItemForDeleted(searchRecordsRequest);
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

    private List<BibItem> searchByBib(SearchRecordsRequest searchRecordsRequest) throws SolrServerException, IOException {
        List<BibItem> bibItems = new ArrayList<>();
        SolrQuery queryForParentAndChildCriteria = solrQueryBuilder.getQueryForParentAndChildCriteriaForDataDump(searchRecordsRequest);
        queryForParentAndChildCriteria.setStart(searchRecordsRequest.getPageNumber() * searchRecordsRequest.getPageSize());
        queryForParentAndChildCriteria.setRows(searchRecordsRequest.getPageSize());
        queryForParentAndChildCriteria.setSort(RecapConstants.TITLE_SORT, SolrQuery.ORDER.asc);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(queryForParentAndChildCriteria);
        SolrDocumentList bibSolrDocumentList = queryResponse.getResults();
        if(CollectionUtils.isNotEmpty(bibSolrDocumentList)) {
            long numFound = bibSolrDocumentList.getNumFound();
            String totalBibCount = String.valueOf(numFound);
            searchRecordsRequest.setTotalRecordsCount(totalBibCount);
            int totalPagesCount = (int) Math.ceil((double) numFound / (double) searchRecordsRequest.getPageSize());
            searchRecordsRequest.setTotalPageCount(totalPagesCount);
            for (Iterator<SolrDocument> iterator = bibSolrDocumentList.iterator(); iterator.hasNext(); ) {
                SolrDocument bibSolrDocument = iterator.next();
                BibItem bibItem = new BibItem();
                populateBib(bibSolrDocument, bibItem);
                bibItems.add(bibItem);
            }

            List<List<BibItem>> partitionedBibItems = Lists.partition(bibItems, 300);
            for (Iterator<List<BibItem>> iterator = partitionedBibItems.iterator(); iterator.hasNext(); ) {
                List<BibItem> bibItemList = iterator.next();
                populateItemInfo(bibItemList, searchRecordsRequest);
            }
        }

        return bibItems;
    }

    /**
     * Search by item for deleted list.
     *
     * @param searchRecordsRequest the search records request
     * @return the list
     * @throws SolrServerException the solr server exception
     * @throws IOException         the io exception
     */
    public List<BibItem> searchByItemForDeleted(SearchRecordsRequest searchRecordsRequest) throws SolrServerException, IOException {
        List<BibItem> bibItems = new ArrayList<>();
        boolean onlyOrphan = isDeletedOnlyOrphanInstitution(searchRecordsRequest);
        Map<Integer, BibItem> bibItemMap = new HashMap<>();
        if(onlyOrphan){
            bibItems = searchByBibForDeleted(searchRecordsRequest);
            searchByItem(searchRecordsRequest, true, bibItemMap);
            eliminateNonOrphanRecords(bibItemMap);
        } else {
            searchByItem(searchRecordsRequest, true, bibItemMap);
            searchByItem(searchRecordsRequest, false, bibItemMap);
            List<BibItem> bibItemForOrphanBib = searchByBibForDeleted(searchRecordsRequest);
            compareAndSetOnlyOrphanBibs(bibItemMap,bibItemForOrphanBib);
        }
        for(Integer bibId:bibItemMap.keySet()){
            bibItems.add(bibItemMap.get(bibId));
        }
        return bibItems;
    }

    private void eliminateNonOrphanRecords(Map<Integer, BibItem> bibItemMap){
        for(Integer bibId:bibItemMap.keySet()){
            BibliographicEntity fetchedBibliographicEntity = bibliographicDetailsRepository.findByBibliographicId(bibId);
            boolean isBibDeleted = false;
            for(ItemEntity fetchedItemEntity:fetchedBibliographicEntity.getItemEntities()){
                if(fetchedItemEntity.isDeleted() || isChangedToPrivateCGD(fetchedItemEntity)){
                    isBibDeleted = true;
                } else {
                    isBibDeleted = false;
                    break;
                }
            }
            if (!isBibDeleted){
                bibItemMap.remove(bibId);
            }
        }
    }

    private void compareAndSetOnlyOrphanBibs(Map<Integer, BibItem> bibItemMap,List<BibItem> bibItemListForOrphanBib){
        for(BibItem bibItem:bibItemListForOrphanBib){
            if(!bibItemMap.containsKey(bibItem.getBibId())){
                bibItemMap.put(bibItem.getBibId(),bibItem);
            }
        }
    }

    private boolean isChangedToPrivateCGD(ItemEntity fetchedItemEntity){
        if(fetchedItemEntity.getCgdChangeLog()!=null){
            if(fetchedItemEntity.getCgdChangeLog().equals(RecapConstants.CGD_CHANGE_LOG_SHARED_TO_PRIVATE)
                    || fetchedItemEntity.getCgdChangeLog().equals(RecapConstants.CGD_CHANGE_LOG_OPEN_TO_PRIVATE)){
                return true;
            }
        }
        return false;
    }

    private List<BibItem> searchByBibForDeleted(SearchRecordsRequest searchRecordsRequest) throws SolrServerException, IOException {
        List<BibItem> bibItems = new ArrayList<>();
        SolrQuery queryForParentAndChildCriteria = solrQueryBuilder.getQueryForParentAndChildCriteriaForDeletedDataDump(searchRecordsRequest);
        queryForParentAndChildCriteria.setStart(searchRecordsRequest.getPageNumber() * searchRecordsRequest.getPageSize());
        queryForParentAndChildCriteria.setRows(searchRecordsRequest.getPageSize());
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(queryForParentAndChildCriteria);
        SolrDocumentList bibSolrDocumentList = queryResponse.getResults();
        if(CollectionUtils.isNotEmpty(bibSolrDocumentList)) {
            long numFound = bibSolrDocumentList.getNumFound();
            String totalBibCount = String.valueOf(numFound);
            searchRecordsRequest.setTotalRecordsCount(totalBibCount);
            int totalPagesCount = (int) Math.ceil((double) numFound / (double) searchRecordsRequest.getPageSize());
            searchRecordsRequest.setTotalPageCount(totalPagesCount);
            for (Iterator<SolrDocument> iterator = bibSolrDocumentList.iterator(); iterator.hasNext(); ) {
                SolrDocument bibSolrDocument = iterator.next();
                BibItem bibItem = new BibItem();
                populateBib(bibSolrDocument, bibItem);
                bibItems.add(bibItem);
            }

            List<List<BibItem>> partitionedBibItems = Lists.partition(bibItems, 300);
            for (Iterator<List<BibItem>> iterator = partitionedBibItems.iterator(); iterator.hasNext(); ) {
                List<BibItem> bibItemList = iterator.next();
                populateItemInfo(bibItemList, searchRecordsRequest);
            }
        }

        return bibItems;
    }

    /**
     * Search by items based on the given search request and return bib items.
     *
     * @param searchRecordsRequest  the search records request
     * @param isCGDChangedToPrivate the is cgd changed to private
     * @return the list
     * @throws SolrServerException the solr server exception
     * @throws IOException         the io exception
     */
    public void searchByItem(SearchRecordsRequest searchRecordsRequest,boolean isCGDChangedToPrivate,Map<Integer, BibItem> bibItemMap) throws SolrServerException, IOException {
        SolrQuery queryForChildAndParentCriteria = solrQueryBuilder.getDeletedQueryForDataDump(searchRecordsRequest,isCGDChangedToPrivate);
        queryForChildAndParentCriteria.setStart(searchRecordsRequest.getPageNumber() * searchRecordsRequest.getPageSize());
        queryForChildAndParentCriteria.setRows(searchRecordsRequest.getPageSize());
        queryForChildAndParentCriteria.setSort(RecapConstants.TITLE_SORT, SolrQuery.ORDER.asc);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(queryForChildAndParentCriteria);
        SolrDocumentList itemSolrDocumentList = queryResponse.getResults();
        if(CollectionUtils.isNotEmpty(itemSolrDocumentList)) {
            long numFound = itemSolrDocumentList.getNumFound();
            String totalItemCount = String.valueOf(numFound);
            searchRecordsRequest.setTotalRecordsCount(totalItemCount);
            int totalPagesCount = (int) Math.ceil((double) numFound / (double) searchRecordsRequest.getPageSize());
            searchRecordsRequest.setTotalPageCount(totalPagesCount);
            for (Iterator<SolrDocument> iterator = itemSolrDocumentList.iterator(); iterator.hasNext(); ) {
                SolrDocument itemSolrDocument = iterator.next();
                Item item = getItem(itemSolrDocument);
                getBibForItems(item, bibItemMap);
            }
        }
    }

    /**
     * Populate item info for the given search request.
     *
     * @param bibItems             the bib items
     * @param searchRecordsRequest the search records request
     */
    public void populateItemInfo(List<BibItem> bibItems, SearchRecordsRequest searchRecordsRequest) {

        String queryStringForMatchParentReturnChild = solrQueryBuilder.getQueryStringForMatchParentReturnChild(searchRecordsRequest);
        String querForItemString = "_root_:" + getRootIds(bibItems) + and + RecapConstants.DOCTYPE + ":" + RecapConstants.ITEM + and
                + queryStringForMatchParentReturnChild + and + RecapConstants.IS_DELETED_ITEM + ":" + searchRecordsRequest.isDeleted() + and + RecapConstants.ITEM_CATALOGING_STATUS + ":"
                + RecapConstants.COMPLETE_STATUS;

        SolrQuery solrQueryForItem = solrQueryBuilder.getSolrQueryForBibItem(querForItemString) ;
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
                Item item = getItem(solrDocument);
                if (getRootIds(bibItems).contains(item.getRoot())) {
                    BibItem bibItem = findBibItem(bibItems, item.getRoot());
                    if (null != bibItem) {
                        bibItem.addItem(item);
                    }
                }
            }
        } catch (IOException|SolrServerException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
    }

    private BibItem findBibItem(List<BibItem> bibItems, String root) {
        for (Iterator<BibItem> iterator = bibItems.iterator(); iterator.hasNext(); ) {
            BibItem bibItem = iterator.next();
            if(bibItem.getRoot().equals(root)){
                return bibItem;
            }
        }
        return null;
    }

    private String getRootIds(List<BibItem> bibItems) {
        StringBuilder rootIds = new StringBuilder();
        rootIds.append("(");
        for (Iterator<BibItem> iterator = bibItems.iterator(); iterator.hasNext(); ) {
            BibItem bibItem = iterator.next();
            rootIds.append(bibItem.getRoot());
            if(iterator.hasNext()){
                rootIds.append(or);
            }
        }
        rootIds.append(")");
        return rootIds.toString();
    }

    private void getBibForItems(Item item, Map<Integer, BibItem> bibItemMap) {
        try {
            List<Integer> itemBibIdList = item.getItemBibIdList();
            if(CollectionUtils.isNotEmpty(itemBibIdList)) {
                for(Integer bibId : itemBibIdList) {
                    if(bibItemMap.containsKey(bibId)) {
                        BibItem bibItem = bibItemMap.get(bibId);
                        bibItem.addItem(item);
                    } else {
                        BibItem bibItem = new BibItem();
                        bibItem.setBibId(bibId);
                        bibItem.addItem(item);
                        bibItemMap.put(bibId, bibItem);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
    }

    /**
     * Populate bib based on the given solr document and bib item.
     *
     * @param solrDocument the solr document
     * @param bibItem      the bib item
     */
    public void populateBib(SolrDocument solrDocument, BibItem bibItem) {
        Collection<String> fieldNames = solrDocument.getFieldNames();
        for (Iterator<String> stringIterator = fieldNames.iterator(); stringIterator.hasNext(); ) {
            String fieldName = stringIterator.next();
            Object fieldValue = solrDocument.getFieldValue(fieldName);
            for (Iterator<BibValueResolver> valueResolverIterator = getBibValueResolversForDataDump().iterator(); valueResolverIterator.hasNext(); ) {
                BibValueResolver valueResolver = valueResolverIterator.next();
                if (valueResolver.isInterested(fieldName)) {
                    valueResolver.setValue(bibItem, fieldValue);
                }
            }
        }
    }

    private Item getItem(SolrDocument itemSolrDocument) {
        Item item = new Item();
        Collection<String> fieldNames = itemSolrDocument.getFieldNames();
        for (Iterator<String> iterator = fieldNames.iterator(); iterator.hasNext(); ) {
            String fieldName = iterator.next();
            Object fieldValue = itemSolrDocument.getFieldValue(fieldName);
            for (Iterator<ItemValueResolver> itemValueResolverIterator = getItemValueResolversForDataDump().iterator(); itemValueResolverIterator.hasNext(); ) {
                ItemValueResolver itemValueResolver = itemValueResolverIterator.next();
                if (itemValueResolver.isInterested(fieldName)) {
                    itemValueResolver.setValue(item, fieldValue);
                }
            }
        }
        return item;
    }

    @Override
    public Integer getPageNumberOnPageSizeChange(SearchRecordsRequest searchRecordsRequest) {
        return null;
    }

    /**
     * Gets bib value resolvers for data dump.
     *
     * @return the bib value resolvers for data dump
     */
    public List<BibValueResolver> getBibValueResolversForDataDump() {
        if (null == bibValueResolvers) {
            bibValueResolvers = new ArrayList<>();
            bibValueResolvers.add(new RootValueResolver());
            bibValueResolvers.add(new BibIdValueResolver());
            bibValueResolvers.add(new DocTypeValueResolver());
            bibValueResolvers.add(new IdValueResolver());
            bibValueResolvers.add(new IsDeletedBibValueResolver());
        }
        return bibValueResolvers;
    }

    /**
     * Gets item value resolvers for data dump.
     *
     * @return the item value resolvers for data dump
     */
    public List<ItemValueResolver> getItemValueResolversForDataDump() {
        if (null == itemValueResolvers) {
            itemValueResolvers = new ArrayList<>();
            itemValueResolvers.add(new org.recap.model.search.resolver.impl.item.DocTypeValueResolver());
            itemValueResolvers.add(new ItemRootValueResolver());
            itemValueResolvers.add(new ItemIdValueResolver());
            itemValueResolvers.add(new org.recap.model.search.resolver.impl.item.IdValueResolver());
            itemValueResolvers.add(new IsDeletedItemValueResolver());
            itemValueResolvers.add(new ItemBibIdValueResolver());
        }
        return itemValueResolvers;
    }

    private List<String> getInstitutionList(String institutionString){
        List<String> institutionList = Arrays.asList(institutionString.split("\\s*,\\s*"));
        return institutionList;
    }

    private boolean isDeletedOnlyOrphanInstitution(SearchRecordsRequest searchRecordsRequest){
        String requestingInstitution = searchRecordsRequest.getRequestingInstitution();
        List<String> deleteOnlyOrphanInstitutionList = getInstitutionList(deletedOnlyOrphanInstitution);
        if(deleteOnlyOrphanInstitutionList.contains(requestingInstitution)){
            return true;
        } else {
            return false;
        }
    }
}
