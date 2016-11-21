package org.recap.camel.processor;

import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by angelind on 27/10/16.
 */
@Component
public class MatchingAlgorithmProcessor {

    @Autowired
    MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Autowired
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    public void saveMatchingMatchPointEntity(List<MatchingMatchPointsEntity> matchingMatchPointsEntities){
        matchingMatchPointsDetailsRepository.save(matchingMatchPointsEntities);
    }

    public void saveMatchingBibEntity(List<MatchingBibEntity> matchingBibEntities){
        matchingBibDetailsRepository.save(matchingBibEntities);
    }

    public void saveMatchingReportEntity(List<ReportEntity> reportEntityList) {
        reportDetailRepository.save(reportEntityList);
    }
}
