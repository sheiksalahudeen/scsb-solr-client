package org.recap.repository.solr.impl;

import org.apache.commons.lang3.StringUtils;
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
import org.recap.model.search.resolver.impl.item.*;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Item;
import org.recap.repository.solr.main.CustomDocumentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

/**
 * Created by rajeshbabuk on 8/7/16.
 */
@Repository
public class BibSolrDocumentRepositoryImpl implements CustomDocumentRepository {

    @Resource
    private SolrTemplate solrTemplate;

    List<BibValueResolver> bibValueResolvers;
    List<ItemValueResolver> itemValueResolvers;

    @Override
    public List<BibItem> search(SearchRecordsRequest searchRecordsRequest, Pageable page) {
        List<BibItem> bibItems = new ArrayList<>();
        try {
            SolrQuery solrQuery = new SolrQuery(searchRecordsRequest.getFieldName()+":"+searchRecordsRequest.getFieldValue());
            solrQuery.setParam("fl", "*,[child parentFilter=DocType:Bib]");

            QueryResponse queryResponse = solrTemplate.getSolrClient().query(solrQuery);

            SolrDocumentList bibSolrDocuments = queryResponse.getResults();
            for (Iterator<SolrDocument> iterator = bibSolrDocuments.iterator(); iterator.hasNext(); ) {
                SolrDocument solrDocument =  iterator.next();
                BibItem bibItem = new BibItem();
                populateBibItem(solrDocument, bibItem);
                List<SolrDocument> holdingsSolrDocuments = solrDocument.getChildDocuments();
                List<Item> items = new ArrayList<>();
                if (!CollectionUtils.isEmpty(holdingsSolrDocuments)) {
                    for (Iterator<SolrDocument> solrDocumentIterator = holdingsSolrDocuments.iterator(); solrDocumentIterator.hasNext(); ) {
                        SolrDocument childSolrDocument = solrDocumentIterator.next();
                        if (childSolrDocument.getFieldValue("DocType").equals("Holdings")) {
                            populateBibItem(childSolrDocument, bibItem);
                        } else if (childSolrDocument.getFieldValue("DocType").equals("Item")){
                            Item item = getItem(childSolrDocument);
                            items.add(item);
                        }
                    }
                }
                bibItem.setItems(items);
                bibItems.add(bibItem);
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bibItems;
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
                if(itemValueResolver.isInterested(fieldName)){
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
                BibValueResolver valueResolver =  valueResolverIterator.next();
                if(valueResolver.isInterested(fieldName)){
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
            }
            else {
                modifiedText.append(character);
            }
            character = stringCharacterIterator.next();
        }
        return modifiedText.toString();
    }

    public List<BibValueResolver> getBibValueResolvers() {
        if (null == bibValueResolvers) {
            bibValueResolvers = new ArrayList<>();
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
            itemValueResolvers.add(new AvailabilityValueResolver());
            itemValueResolvers.add(new BarcodeValueResolver());
            itemValueResolvers.add(new CallNumberValueResolver());
            itemValueResolvers.add(new CollectionGroupDesignationValueResolver());
            itemValueResolvers.add(new CustomerCodeValueResolver());
            itemValueResolvers.add(new org.recap.model.search.resolver.impl.item.DocTypeValueResolver());
            itemValueResolvers.add(new ItemOwningInstitutionValueResolver());
            itemValueResolvers.add(new UseRestrictionValueResolver());
            itemValueResolvers.add(new VolumePartYearValueResolver());
        }
        return itemValueResolvers;
    }
}
