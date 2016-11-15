package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.solr.BibItem;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.repository.solr.impl.BibSolrDocumentRepositoryImpl;
import org.recap.util.MatchingAlgorithmUtil;
import org.recap.util.SolrQueryBuilder;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by angelind on 4/11/16.
 */
public class SaveMatchingBibsCallable implements Callable {

    private MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;
    private String matchCriteria;
    private SolrTemplate solrTemplate;
    private BibSolrDocumentRepositoryImpl bibSolrDocumentRepository;
    private ProducerTemplate producer;
    private SolrQueryBuilder solrQueryBuilder;
    private long batchSize;
    private int pageNum;
    private MatchingAlgorithmUtil matchingAlgorithmUtil;
    private static List<Integer> bibIdList;

    public SaveMatchingBibsCallable() {
    }

    public SaveMatchingBibsCallable(MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository, String matchCriteria,
                                    SolrTemplate solrTemplate, BibSolrDocumentRepositoryImpl bibSolrDocumentRepository,
                                    ProducerTemplate producer, SolrQueryBuilder solrQueryBuilder, long batchSize, int pageNum, MatchingAlgorithmUtil matchingAlgorithmUtil) {
        this.matchingMatchPointsDetailsRepository = matchingMatchPointsDetailsRepository;
        this.matchCriteria = matchCriteria;
        this.solrTemplate = solrTemplate;
        this.bibSolrDocumentRepository = bibSolrDocumentRepository;
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
                bibSolrDocumentRepository.populateBibItem(solrDocument, bibItem);
                Integer bibId = bibItem.getBibId();
                if(!getBibIdList().contains(bibId)) {
                    MatchingBibEntity matchingBibEntity = new MatchingBibEntity();
                    matchingBibEntity.setBibId(bibId);
                    matchingBibEntity.setRoot(bibItem.getRoot());
                    matchingBibEntity.setOwningInstitution(bibItem.getOwningInstitution());
                    matchingBibEntity.setOwningInstBibId(bibItem.getOwningInstitutionBibId());
                    matchingBibEntity.setTitle(bibItem.getTitleDisplay());
                    matchingBibEntity.setOclc(matchingAlgorithmUtil.populateOCLC(matchingCriteriaValues, bibItem, matchCriteria));
                    matchingBibEntity.setIsbn(matchingAlgorithmUtil.populateISBN(matchingCriteriaValues, bibItem, matchCriteria));
                    matchingBibEntity.setIssn(matchingAlgorithmUtil.populateISSN(matchingCriteriaValues, bibItem, matchCriteria));
                    matchingBibEntity.setLccn(bibItem.getLccn());
                    matchingBibEntity.setMaterialType(bibItem.getLeaderMaterialType());
                    matchingBibEntity.setMatching(matchCriteria);
                    matchingBibEntityList.add(matchingBibEntity);
                    addBibIdToList(bibId);
                }
            }
            if (CollectionUtils.isNotEmpty(matchingBibEntityList)) {
                producer.sendBody("scsbactivemq:queue:saveMatchingBibsQ", matchingBibEntityList);
                size = size + matchingBibEntityList.size();
            }
        }
        return size;
    }

    public static void addBibIdToList(Integer bibId) {
        getBibIdList().add(bibId);
    }

    public static List<Integer> getBibIdList() {
        if(bibIdList == null) {
            bibIdList = new ArrayList<>();
        }
        return bibIdList;
    }

    public static void setBibIdList(List<Integer> bibIdList) {
        SaveMatchingBibsCallable.bibIdList = bibIdList;
    }
}
