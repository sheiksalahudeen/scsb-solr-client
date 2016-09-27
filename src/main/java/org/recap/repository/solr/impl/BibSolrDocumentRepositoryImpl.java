package org.recap.repository.solr.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Holdings;
import org.recap.model.solr.Item;
import org.recap.repository.solr.main.CustomDocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.RequestMethod;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Join;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.CharacterIterator;
import java.text.NumberFormat;
import java.text.StringCharacterIterator;
import java.util.*;

/**
 * Created by rajeshbabuk on 8/7/16.
 */
@Repository
public class BibSolrDocumentRepositoryImpl implements CustomDocumentRepository {

    @Resource
    private SolrTemplate solrTemplate;

    @Override
    public List<BibItem> search(SearchRecordsRequest searchRecordsRequest, Pageable page) {

        Criteria criteriaForFieldName = getCriteriaForFieldName(searchRecordsRequest);

        SimpleQuery bibQuery = new SimpleQuery();
        bibQuery.setPageRequest(page);
        //bibQuery.addSort(new Sort(Sort.Direction.ASC, RecapConstants.TITLE_SORT));
        bibQuery.addCriteria(criteriaForFieldName);
        bibQuery.addFilterQuery(getBibFilterQueryForInputFields(searchRecordsRequest, bibQuery));
        bibQuery.addFilterQuery(new SimpleFilterQuery(new Criteria(RecapConstants.DOCTYPE).is(RecapConstants.BIB)));

        Page bibItemResults = solrTemplate.queryForPage(bibQuery, BibItem.class);

        SimpleQuery itemQuery = new SimpleQuery();
        itemQuery.setPageRequest(new PageRequest(0,1));
        itemQuery.addCriteria(criteriaForFieldName);
        itemQuery.addFilterQuery(getItemFilterQueryForInputFields(searchRecordsRequest, itemQuery));
        itemQuery.addFilterQuery(new SimpleFilterQuery(new Criteria(RecapConstants.DOCTYPE).is(RecapConstants.ITEM)));

        Page itemResults = solrTemplate.queryForPage(itemQuery, Item.class);
        String totalItemCount = NumberFormat.getNumberInstance().format(itemResults.getTotalElements());
        searchRecordsRequest.setTotalItemRecordsCount(totalItemCount);

        searchRecordsRequest.setPageNumber(bibItemResults.getNumber());
        searchRecordsRequest.setTotalPageCount(bibItemResults.getTotalPages());
        String totalBibCount = NumberFormat.getNumberInstance().format(bibItemResults.getTotalElements());
        searchRecordsRequest.setTotalBibRecordsCount(totalBibCount);
        List<BibItem> bibItems = buildBibItems(bibItemResults);
        return bibItems;
    }

    private SimpleFilterQuery getBibFilterQueryForInputFields(SearchRecordsRequest searchRecordsRequest, SimpleQuery query) {
        SimpleFilterQuery filterQuery = new SimpleFilterQuery();
        String fieldName = searchRecordsRequest.getFieldName();
        List<String> useRestrictions = new ArrayList<>();
        useRestrictions.addAll(searchRecordsRequest.getUseRestrictions());

        if (!isEmptySearch(searchRecordsRequest) || StringUtils.isBlank(fieldName)) {
            query.setJoin(Join.from(RecapConstants.HOLDINGS_ID).to(RecapConstants.HOLDINGS_ID));
        }
        if (!CollectionUtils.isEmpty(searchRecordsRequest.getOwningInstitutions())) {
            query.addFilterQuery(new SimpleFilterQuery(new Criteria(RecapConstants.OWNING_INSTITUTION).in(searchRecordsRequest.getOwningInstitutions())));
        }
        if (!CollectionUtils.isEmpty(searchRecordsRequest.getMaterialTypes())) {
            query.addFilterQuery(new SimpleFilterQuery(new Criteria(RecapConstants.LEADER_MATERIAL_TYPE).in(searchRecordsRequest.getMaterialTypes())));
        }
        if (!CollectionUtils.isEmpty(searchRecordsRequest.getCollectionGroupDesignations()) || !CollectionUtils.isEmpty(searchRecordsRequest.getAvailability())
                || !CollectionUtils.isEmpty(useRestrictions)) {
            filterQuery.setJoin(Join.from(RecapConstants.HOLDINGS_ID).to(RecapConstants.HOLDINGS_ID));
        }
        if (!CollectionUtils.isEmpty(searchRecordsRequest.getCollectionGroupDesignations())) {
            filterQuery.addCriteria(new Criteria(RecapConstants.COLLECTION_GROUP_DESIGNATION).in(searchRecordsRequest.getCollectionGroupDesignations()));
        }
        if (!CollectionUtils.isEmpty(searchRecordsRequest.getAvailability())) {
            filterQuery.addCriteria(new Criteria(RecapConstants.AVAILABILITY).in(searchRecordsRequest.getAvailability()));
        }
        if (!CollectionUtils.isEmpty(useRestrictions)) {
            filterQuery.addCriteria(new Criteria(RecapConstants.USE_RESTRICTION).in(useRestrictions));
        }
        return filterQuery;
    }

    private SimpleFilterQuery getItemFilterQueryForInputFields(SearchRecordsRequest searchRecordsRequest, SimpleQuery query) {
        SimpleFilterQuery filterQuery = new SimpleFilterQuery();
        String fieldName = searchRecordsRequest.getFieldName();
        List<String> useRestrictions = new ArrayList<>();
        useRestrictions.addAll(searchRecordsRequest.getUseRestrictions());

        if (!isEmptySearch(searchRecordsRequest) || StringUtils.isBlank(fieldName)) {
            query.setJoin(Join.from(RecapConstants.HOLDINGS_ID).to(RecapConstants.HOLDINGS_ID));
        }
        if (!CollectionUtils.isEmpty(searchRecordsRequest.getOwningInstitutions()) || !CollectionUtils.isEmpty(searchRecordsRequest.getMaterialTypes())) {
            filterQuery.setJoin(Join.from(RecapConstants.HOLDINGS_ID).to(RecapConstants.HOLDINGS_ID));
        }
        if (!CollectionUtils.isEmpty(searchRecordsRequest.getOwningInstitutions())) {
            filterQuery.addCriteria(new Criteria(RecapConstants.OWNING_INSTITUTION).in(searchRecordsRequest.getOwningInstitutions()));
        }
        if (!CollectionUtils.isEmpty(searchRecordsRequest.getMaterialTypes())) {
            filterQuery.addCriteria(new Criteria(RecapConstants.LEADER_MATERIAL_TYPE).in(searchRecordsRequest.getMaterialTypes()));
        }
        if (!CollectionUtils.isEmpty(searchRecordsRequest.getCollectionGroupDesignations())) {
            query.addFilterQuery(new SimpleFilterQuery(new Criteria(RecapConstants.COLLECTION_GROUP_DESIGNATION).in(searchRecordsRequest.getCollectionGroupDesignations())));
        }
        if (!CollectionUtils.isEmpty(searchRecordsRequest.getAvailability())) {
            query.addFilterQuery(new SimpleFilterQuery(new Criteria(RecapConstants.AVAILABILITY).in(searchRecordsRequest.getAvailability())));
        }
        if (!CollectionUtils.isEmpty(useRestrictions)) {
            query.addFilterQuery(new SimpleFilterQuery(new Criteria(RecapConstants.USE_RESTRICTION).in(useRestrictions)));
        }
        return filterQuery;
    }

    public Criteria getCriteriaForFieldName(SearchRecordsRequest searchRecordsRequest) {
        Criteria criteria = null;
        String fieldName = searchRecordsRequest.getFieldName();
        String fieldValue = getModifiedText(searchRecordsRequest.getFieldValue().trim());

        if (StringUtils.isBlank(fieldName) && StringUtils.isBlank(fieldValue)) {
            criteria = new Criteria().expression(RecapConstants.ALL);
        } else if (StringUtils.isBlank(fieldName)) {
            fieldValue = StringUtils.join(fieldValue.split("\\s+"), " " + RecapConstants.AND + " ");
            criteria = new Criteria().expression(fieldValue);
        } else if (StringUtils.isBlank(fieldValue)) {
            if (RecapConstants.TITLE_STARTS_WITH.equals(fieldName)) {
                fieldName = RecapConstants.TITLE;
            }
            criteria = new Criteria(fieldName).expression(RecapConstants.ALL);
        } else {
            if (RecapConstants.TITLE_STARTS_WITH.equals(fieldName)) {
                String[] splitedTitle = fieldValue.split(" ");
                criteria = new Criteria(RecapConstants.TITLE_STARTS_WITH).startsWith(splitedTitle[0]);
            } else {
                String[] splitValues = fieldValue.split("\\s+");
                for (String splitValue : splitValues) {
                    if (null == criteria) {
                        criteria = new Criteria().and(fieldName).expression(splitValue);
                    } else {
                        criteria.and(fieldName).expression(splitValue);
                    }
                }
            }
        }
        return criteria;
    }

    private List<BibItem> buildBibItems(Page results) {
        List<BibItem> bibItems = results.getContent();
        Set<Integer> itemIds = new HashSet<>();
        Set<Integer> holdingsIds = new HashSet<>();
        if (!CollectionUtils.isEmpty(bibItems)) {
            for (BibItem bibItem : bibItems) {
                if (!CollectionUtils.isEmpty(bibItem.getBibItemIdList())) {
                    itemIds.addAll(bibItem.getBibItemIdList());
                }
                if(!CollectionUtils.isEmpty(bibItem.getHoldingsIdList())) {
                    holdingsIds.add(bibItem.getHoldingsIdList().get(0));
                }
            }
        }
        Map<Integer, Item> itemMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(itemIds)) {
            List<List<Integer>> partitions = Lists.partition(new ArrayList<Integer>(itemIds), 1000);
            for (List<Integer> partitionItemIds : partitions) {
                SimpleQuery query = new SimpleQuery(new Criteria(RecapConstants.ITEM_ID).in(partitionItemIds));
                query.setRows(partitionItemIds.size());
                ScoredPage<Item> itemsPage = solrTemplate.queryForPage(query, Item.class, RequestMethod.POST);
                if (itemsPage.getTotalElements() > partitionItemIds.size()) {
                    query.setRows(Math.toIntExact(itemsPage.getTotalElements()));
                    itemsPage = solrTemplate.queryForPage(query, Item.class, RequestMethod.POST);
                }
                List<Item> items = itemsPage.getContent();
                if (!CollectionUtils.isEmpty(items)) {
                    for (Item item : items) {
                        itemMap.put(item.getItemId(), item);
                    }
                }
            }
        }
        Map<Integer, Holdings> holdingsMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(holdingsIds)) {
            List<List<Integer>> partitions = Lists.partition(new ArrayList<>(holdingsIds), 1000);
            for(List<Integer> partitionHoldingsIds : partitions) {
                SimpleQuery query = new SimpleQuery(new Criteria(RecapConstants.HOLDING_ID).in(partitionHoldingsIds));
                query.setRows(partitionHoldingsIds.size());
                ScoredPage<Holdings> holdingsPage = solrTemplate.queryForPage(query, Holdings.class, RequestMethod.POST);
                if(holdingsPage.getTotalElements() > partitionHoldingsIds.size()) {
                    query.setRows(Math.toIntExact(holdingsPage.getTotalElements()));
                    holdingsPage = solrTemplate.queryForPage(query, Holdings.class, RequestMethod.POST);
                }
                List<Holdings> holdingsList = holdingsPage.getContent();
                if(!CollectionUtils.isEmpty(holdingsList)) {
                    for(Holdings holdings : holdingsList) {
                        holdingsMap.put(holdings.getHoldingsId(), holdings);
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(bibItems)) {
            for (BibItem bibItem : bibItems) {
                if (!CollectionUtils.isEmpty(bibItem.getBibItemIdList())) {
                    for (Integer itemId : bibItem.getBibItemIdList()) {
                        bibItem.getItems().add(itemMap.get(itemId));
                    }
                }
                List<Integer> holdingsIdList = bibItem.getHoldingsIdList();
                if (!CollectionUtils.isEmpty(holdingsIdList)) {
                    Holdings holdings = holdingsMap.get(holdingsIdList.get(0));
                    bibItem.setSummaryHoldings(holdings!= null ? holdings.getSummaryHoldings() : "");
                }
            }
        }
        return bibItems;
    }

    private boolean isEmptySearch(SearchRecordsRequest searchRecordsRequest) {
        boolean emptySearch = false;
        if (CollectionUtils.isEmpty(searchRecordsRequest.getOwningInstitutions()) && CollectionUtils.isEmpty(searchRecordsRequest.getMaterialTypes()) &&
                CollectionUtils.isEmpty(searchRecordsRequest.getCollectionGroupDesignations()) && CollectionUtils.isEmpty(searchRecordsRequest.getAvailability())) {
            emptySearch = true;
        }
        return emptySearch;
    }

    public String getModifiedText(String searchText) {
        StringBuffer modifiedText = new StringBuffer();
        StringCharacterIterator stringCharacterIterator = new StringCharacterIterator(searchText);
        char character = stringCharacterIterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '\\') {
                modifiedText.append("\\\\");
            } else if (character == '?') {
                modifiedText.append("\\?");
            } else if (character == '*') {
                modifiedText.append("\\*");
            } else if (character == '+') {
                modifiedText.append("\\+");
            } else if (character == ':') {
                modifiedText.append("\\:");
            } else if (character == '{') {
                modifiedText.append("\\{");
            } else if (character == '}') {
                modifiedText.append("\\}");
            } else if (character == '[') {
                modifiedText.append("\\[");
            } else if (character == ']') {
                modifiedText.append("\\]");
            } else if (character == '(') {
                modifiedText.append("\\(");
            } else if (character == ')') {
                modifiedText.append("\\)");
            } else if (character == '^') {
                modifiedText.append("\\^");
            } else if (character == '~') {
                modifiedText.append("\\~");
            } else if (character == '-') {
                modifiedText.append("\\-");
            } else if (character == '!') {
                modifiedText.append("\\!");
            } else if (character == '\'') {
                modifiedText.append("\\'");
            } else if (character == '@') {
                modifiedText.append("\\@");
            } else if (character == '#') {
                modifiedText.append("\\#");
            } else if (character == '$') {
                modifiedText.append("\\$");
            } else if (character == '%') {
                modifiedText.append("\\%");
            } else if (character == '/') {
                modifiedText.append("\\/");
            } else if (character == '"') {
                modifiedText.append("\\\"");
            } else if (character == '.') {
                modifiedText.append("\\.");
            }
            else {
                modifiedText.append(character);
            }
            character = stringCharacterIterator.next();
        }
        return modifiedText.toString();
    }
}
