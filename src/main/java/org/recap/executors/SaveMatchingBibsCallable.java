package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.search.resolver.impl.bib.*;
import org.recap.model.solr.BibItem;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.util.MatchingAlgorithmUtil;
import org.recap.util.SolrQueryBuilder;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by angelind on 4/11/16.
 */
public class SaveMatchingBibsCallable implements Callable {

    private MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;
    private String matchCriteria;
    private SolrTemplate solrTemplate;
    private ProducerTemplate producer;
    private SolrQueryBuilder solrQueryBuilder;
    private long batchSize;
    private int pageNum;
    private MatchingAlgorithmUtil matchingAlgorithmUtil;

    List<BibValueResolver> bibValueResolvers;
    private static Set<Integer> bibIdList;

    public SaveMatchingBibsCallable() {
        //Do nothing
    }

    public SaveMatchingBibsCallable(MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository, String matchCriteria,
                                    SolrTemplate solrTemplate,
                                    ProducerTemplate producer, SolrQueryBuilder solrQueryBuilder, long batchSize, int pageNum, MatchingAlgorithmUtil matchingAlgorithmUtil) {
        this.matchingMatchPointsDetailsRepository = matchingMatchPointsDetailsRepository;
        this.matchCriteria = matchCriteria;
        this.solrTemplate = solrTemplate;
        this.producer = producer;
        this.solrQueryBuilder = solrQueryBuilder;
        this.batchSize = batchSize;
        this.pageNum = pageNum;
        this.matchingAlgorithmUtil = matchingAlgorithmUtil;
    }

    @Override
    public Object call() throws Exception {
        Integer size = 0;
        long from = pageNum * batchSize;
        List<MatchingMatchPointsEntity> matchPointsEntityList = matchingMatchPointsDetailsRepository.getMatchPointEntityByCriteria(matchCriteria, from, batchSize);
        if(CollectionUtils.isNotEmpty(matchPointsEntityList)) {
            List<String> matchingCriteriaValues = new ArrayList<>();
            SolrQuery solrQuery = solrQueryBuilder.solrQueryToFetchBibDetails(matchPointsEntityList, matchingCriteriaValues, matchCriteria);
            QueryResponse queryResponse = solrTemplate.getSolrClient().query(solrQuery);
            SolrDocumentList solrDocumentList = queryResponse.getResults();
            List<MatchingBibEntity> matchingBibEntityList = new ArrayList<>();
            for (Iterator<SolrDocument> iterator = solrDocumentList.iterator(); iterator.hasNext(); ) {
                SolrDocument solrDocument = iterator.next();
                BibItem bibItem = new BibItem();
                populateBibItem(solrDocument, bibItem);
                Integer bibId = bibItem.getBibId();
                if (!isBibIdDuplicate(bibId)) {
                    MatchingBibEntity matchingBibEntity = new MatchingBibEntity();
                    matchingBibEntity.setBibId(bibId);
                    matchingBibEntity.setRoot(bibItem.getRoot());
                    matchingBibEntity.setOwningInstitution(bibItem.getOwningInstitution());
                    matchingBibEntity.setOwningInstBibId(bibItem.getOwningInstitutionBibId());
                    matchingBibEntity.setTitle(bibItem.getTitleDisplay());
                    matchingBibEntity.setOclc(CollectionUtils.isNotEmpty(bibItem.getOclcNumber()) ? StringUtils.join(bibItem.getOclcNumber(), ",") : null);
                    matchingBibEntity.setIsbn(CollectionUtils.isNotEmpty(bibItem.getIsbn()) ? StringUtils.join(bibItem.getIsbn(), ",") : null);
                    matchingBibEntity.setIssn(CollectionUtils.isNotEmpty(bibItem.getIssn()) ? StringUtils.join(bibItem.getIssn(), ",") : null);
                    matchingBibEntity.setLccn(bibItem.getLccn());
                    matchingBibEntity.setMaterialType(bibItem.getLeaderMaterialType());
                    matchingBibEntity.setMatching(matchCriteria);
                    if(addBibIdToList(bibId)) {
                        matchingBibEntityList.add(matchingBibEntity);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(matchingBibEntityList)) {
                producer.sendBody("scsbactivemq:queue:saveMatchingBibsQ", matchingBibEntityList);
                size = size + matchingBibEntityList.size();
            }
        }
        return size;
    }

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

    public static synchronized boolean addBibIdToList(Integer bibId) {
        return getBibIdList().add(bibId);
    }

    public static synchronized Set<Integer> getBibIdList() {
        if(bibIdList == null) {
            bibIdList = new HashSet<>();
        }
        return bibIdList;
    }

    public static synchronized boolean isBibIdDuplicate(Integer bibId) {
        if(getBibIdList().contains(bibId)) {
            return true;
        }
        return false;
    }

    public static void setBibIdList(Set<Integer> bibIdList) {
        SaveMatchingBibsCallable.bibIdList = bibIdList;
    }

    public List<BibValueResolver> getBibValueResolvers() {
        if (null == bibValueResolvers) {
            bibValueResolvers = new ArrayList<>();
            bibValueResolvers.add(new RootValueResolver());
            bibValueResolvers.add(new BibIdValueResolver());
            bibValueResolvers.add(new IdValueResolver());
            bibValueResolvers.add(new ISBNValueResolver());
            bibValueResolvers.add(new ISSNValueResolver());
            bibValueResolvers.add(new LCCNValueResolver());
            bibValueResolvers.add(new LeaderMaterialTypeValueResolver());
            bibValueResolvers.add(new OCLCValueResolver());
            bibValueResolvers.add(new OwningInstitutionBibIdValueResolver());
            bibValueResolvers.add(new OwningInstitutionValueResolver());
            bibValueResolvers.add(new TitleDisplayValueResolver());
            bibValueResolvers.add(new IsDeletedBibValueResolver());
        }
        return bibValueResolvers;
    }
}
