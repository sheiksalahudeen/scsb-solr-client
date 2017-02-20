package org.recap.camel.processor;

import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by angelind on 27/10/16.
 */
@Component
public class MatchingAlgorithmProcessor {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmProcessor.class);

    @Autowired
    MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Autowired
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    public void saveMatchingMatchPointEntity(List<MatchingMatchPointsEntity> matchingMatchPointsEntities){
        matchingMatchPointsDetailsRepository.save(matchingMatchPointsEntities);
    }

    public void saveMatchingBibEntity(List<MatchingBibEntity> matchingBibEntities){
        try {
            matchingBibDetailsRepository.save(matchingBibEntities);
        } catch (Exception ex) {
            logger.info("Exception : {}",ex);
            for(MatchingBibEntity matchingBibEntity : matchingBibEntities) {
                try {
                    matchingBibDetailsRepository.save(matchingBibEntity);
                } catch (Exception e) {
                    logger.info("Exception for single Entity : " , e);
                    logger.info("ISBN : " + matchingBibEntity.getIsbn());
                }
            }
        }
    }

    public void saveMatchingReportEntity(List<ReportEntity> reportEntityList) {
        reportDetailRepository.save(reportEntityList);
    }

    public void updateItemEntity(List<ItemEntity> itemEntities) {
        itemDetailsRepository.save(itemEntities);
    }
}
