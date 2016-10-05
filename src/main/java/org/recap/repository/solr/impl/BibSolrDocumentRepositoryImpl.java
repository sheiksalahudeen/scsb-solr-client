package org.recap.repository.solr.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.search.resolver.impl.Bib.*;
import org.recap.model.search.resolver.impl.Bib.DocTypeValueResolver;
import org.recap.model.search.resolver.impl.Bib.IdValueResolver;
import org.recap.model.search.resolver.impl.item.*;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Item;
import org.recap.repository.solr.main.CustomDocumentRepository;
import org.recap.util.SolrQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.NumberFormat;
import java.text.StringCharacterIterator;
import java.util.*;

/**
 * Created by rajeshbabuk on 8/7/16.
 */
@Repository
public class BibSolrDocumentRepositoryImpl implements CustomDocumentRepository {

    Logger log = Logger.getLogger(BibSolrDocumentRepositoryImpl.class);

    @Resource
    private SolrTemplate solrTemplate;

    @Autowired
    private SolrQueryBuilder solrQueryBuilder;

    List<BibValueResolver> bibValueResolvers;
    List<ItemValueResolver> itemValueResolvers;

    @Override
    public List<BibItem> search(SearchRecordsRequest searchRecordsRequest) {
        List<BibItem> bibItems = new ArrayList<>();
        try {
            if (isEmptyField(searchRecordsRequest)) {
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
        } catch (SolrServerException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return bibItems;
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
        queryForChildAndParentCriteria.setSort(RecapConstants.TITLE_SORT, SolrQuery.ORDER.asc);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(queryForChildAndParentCriteria);
        SolrDocumentList itemSolrDocumentList = queryResponse.getResults();
        if(CollectionUtils.isNotEmpty(itemSolrDocumentList)) {
            setCountsByItem(searchRecordsRequest, itemSolrDocumentList);
            for (Iterator<SolrDocument> iterator = itemSolrDocumentList.iterator(); iterator.hasNext(); ) {
                SolrDocument itemSolrDocument = iterator.next();
                Item item = getItem(itemSolrDocument);
                bibItems.addAll(getBibItems(item));
            }
        }
        return bibItems;
    }

    private List<BibItem> searchByBib(SearchRecordsRequest searchRecordsRequest) throws SolrServerException, IOException {
        List<BibItem> bibItems = new ArrayList<>();

        SolrQuery queryForParentAndChildCriteria = solrQueryBuilder.getQueryForParentAndChildCriteria(searchRecordsRequest);
        queryForParentAndChildCriteria.setStart(searchRecordsRequest.getPageNumber() * searchRecordsRequest.getPageSize());
        queryForParentAndChildCriteria.setSort(RecapConstants.TITLE_SORT, SolrQuery.ORDER.asc);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(queryForParentAndChildCriteria);
        SolrDocumentList bibSolrDocumentList = queryResponse.getResults();
        if(CollectionUtils.isNotEmpty(bibSolrDocumentList)) {
            setCountsByBib(searchRecordsRequest, bibSolrDocumentList);
            for (Iterator<SolrDocument> iterator = bibSolrDocumentList.iterator(); iterator.hasNext(); ) {
                SolrDocument bibSolrDocument = iterator.next();
                BibItem bibItem = new BibItem();
                populateBibItem(bibSolrDocument, bibItem);
                populateItemInfo(bibItem);
                bibItems.add(bibItem);
            }
        }
        return bibItems;
    }

    private List<BibItem> getBibItems(Item item) {
        List<BibItem> bibItems = new ArrayList<>();
        SolrQuery solrQueryForBib = solrQueryBuilder.getSolrQueryForBibItem("_root_:" + item.getRoot(), RecapConstants.BIB);
        QueryResponse queryResponse = null;
        try {
            queryResponse = solrTemplate.getSolrClient().query(solrQueryForBib);
            SolrDocumentList solrDocuments = queryResponse.getResults();
            for (Iterator<SolrDocument> iterator = solrDocuments.iterator(); iterator.hasNext(); ) {
                SolrDocument solrDocument = iterator.next();
                BibItem bibItem = new BibItem();
                populateBibItem(solrDocument, bibItem);
                bibItem.setItems(Arrays.asList(item));
                bibItems.add(bibItem);
            }
        } catch (SolrServerException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return bibItems;
    }

    private void populateItemInfo(BibItem bibItem) {
        SolrQuery solrQueryForItem = solrQueryBuilder.getSolrQueryForBibItem("_root_:" + bibItem.getRoot(), RecapConstants.ITEM);
        QueryResponse queryResponse = null;
        try {
            queryResponse = solrTemplate.getSolrClient().query(solrQueryForItem);
            SolrDocumentList solrDocuments = queryResponse.getResults();
            for (Iterator<SolrDocument> iterator = solrDocuments.iterator(); iterator.hasNext(); ) {
                SolrDocument solrDocument = iterator.next();
                Item item = getItem(solrDocument);
                bibItem.addItem(item);
            }
        } catch (SolrServerException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private boolean isItemField(SearchRecordsRequest searchRecordsRequest) {
        if (StringUtils.isNotBlank(searchRecordsRequest.getFieldName())
                && (searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.BARCODE) || searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.CALL_NUMBER))) {
            return true;
        }
        return false;
    }

    private void setCountsByBib(SearchRecordsRequest searchRecordsRequest, SolrDocumentList bibSolrDocuments) throws IOException, SolrServerException {
        long numFound = bibSolrDocuments.getNumFound();
        String totalBibCount = NumberFormat.getNumberInstance().format(numFound);
        searchRecordsRequest.setTotalBibRecordsCount(totalBibCount);
        String totalItemCount = NumberFormat.getNumberInstance().format(getItemCountsForBib(searchRecordsRequest));
        searchRecordsRequest.setTotalItemRecordsCount(totalItemCount);
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
        String totalBibCount = NumberFormat.getNumberInstance().format(getBibCountsForItem(searchRecordsRequest));
        searchRecordsRequest.setTotalBibRecordsCount(totalBibCount);
        int totalPagesCount = (int) Math.ceil((double) numFound / (double) searchRecordsRequest.getPageSize());
        searchRecordsRequest.setTotalPageCount(totalPagesCount);
    }

    private long getBibCountsForItem(SearchRecordsRequest searchRecordsRequest) throws IOException, SolrServerException {
        SolrQuery queryForParentAndChildCriteria = solrQueryBuilder.getCountQueryForParentAndChildCriteria(searchRecordsRequest);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(queryForParentAndChildCriteria);
        return queryResponse.getResults().getNumFound();
    }

    private Item getItem(SolrDocument itemSolrDocument) {
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

    private void populateBibItem(SolrDocument solrDocument, BibItem bibItem) {
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

    public Criteria getCriteriaForFieldName(SearchRecordsRequest searchRecordsRequest) {
        Criteria criteria = null;
        String fieldName = searchRecordsRequest.getFieldName();
        String fieldValue = getModifiedText(searchRecordsRequest.getFieldValue().trim());

        if (StringUtils.isBlank(fieldName) && StringUtils.isBlank(fieldValue)) {
            criteria = new Criteria().expression(RecapConstants.ALL);
        } else if (StringUtils.isBlank(fieldName)) {
            fieldValue = "(" + StringUtils.join(fieldValue.split("\\s+"), " " + RecapConstants.AND + " ") + ")";
            criteria = new Criteria().expression(fieldValue)
                    .or(RecapConstants.TITLE_SEARCH).expression(fieldValue)
                    .or(RecapConstants.AUTHOR_SEARCH).expression(fieldValue)
                    .or(RecapConstants.PUBLISHER).expression(fieldValue);
        } else if (StringUtils.isBlank(fieldValue)) {
            if (RecapConstants.TITLE_STARTS_WITH.equals(fieldName)) {
                fieldName = RecapConstants.TITLE_SEARCH;
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
            } else {
                modifiedText.append(character);
            }
            character = stringCharacterIterator.next();
        }
        return modifiedText.toString();
    }

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
        }
        return bibValueResolvers;
    }

    public List<ItemValueResolver> getItemValueResolvers() {
        if (null == itemValueResolvers) {
            itemValueResolvers = new ArrayList<>();
            itemValueResolvers.add(new AvailabilitySearchValueResolver());
            itemValueResolvers.add(new AvailabilityDisplayValueResolver());
            itemValueResolvers.add(new BarcodeValueResolver());
            itemValueResolvers.add(new CallNumberValueResolver());
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
        }
        return itemValueResolvers;
    }
}
