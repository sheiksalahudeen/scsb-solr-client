package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.util.MatchingAlgorithmUtil;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by angelind on 15/11/16.
 */
public class SaveMatchingReportsCallable implements Callable {

    private MatchingBibDetailsRepository matchingBibDetailsRepository;
    private MatchingAlgorithmUtil matchingAlgorithmUtil;
    private ProducerTemplate producer;
    private long from;
    private long batchSize;

    public SaveMatchingReportsCallable(MatchingBibDetailsRepository matchingBibDetailsRepository, MatchingAlgorithmUtil matchingAlgorithmUtil,
                                       ProducerTemplate producer, long from, long batchSize) {
        this.matchingBibDetailsRepository = matchingBibDetailsRepository;
        this.matchingAlgorithmUtil = matchingAlgorithmUtil;
        this.producer = producer;
        this.from = from;
        this.batchSize = batchSize;
    }

    @Override
    public Object call() throws Exception {
        List<Integer> multipleMatchedBibIdsBasedOnLimit = matchingBibDetailsRepository.getMultipleMatchedBibIdsBasedOnLimit(from, batchSize);
        List<MatchingBibEntity> multipleMatchPointBibEntityList = matchingBibDetailsRepository.getBibEntityBasedOnBibIds(multipleMatchedBibIdsBasedOnLimit);
        if (CollectionUtils.isNotEmpty(multipleMatchPointBibEntityList)) {
            List<ReportEntity> reportEntityList = matchingAlgorithmUtil.populateReportEntities(multipleMatchPointBibEntityList);
            if(CollectionUtils.isNotEmpty(reportEntityList)) {
                producer.sendBody("scsbactivemq:queue:saveMatchingReportsQ", reportEntityList);
                return reportEntityList.size();
            }
        }
        return null;
    }
}
