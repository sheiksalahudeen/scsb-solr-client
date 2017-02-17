package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.MatchingAlgorithmCGDProcessor;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.repository.jpa.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by angelind on 6/1/17.
 */
public class MatchingAlgorithmCGDCallable implements Callable {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmCGDCallable.class);

    private ReportDataDetailsRepository reportDataDetailsRepository;
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private int pageNum;
    private Integer batchSize;
    private ProducerTemplate producerTemplate;
    private Map collectionGroupMap;
    private Map institutionMap;
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    public MatchingAlgorithmCGDCallable(ReportDataDetailsRepository reportDataDetailsRepository, BibliographicDetailsRepository bibliographicDetailsRepository,
                                        int pageNum, Integer batchSize, ProducerTemplate producerTemplate, Map collectionGroupMap, Map institutionMap,
                                        ItemChangeLogDetailsRepository itemChangeLogDetailsRepository, CollectionGroupDetailsRepository collectionGroupDetailsRepository) {
        this.reportDataDetailsRepository = reportDataDetailsRepository;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.pageNum = pageNum;
        this.batchSize = batchSize;
        this.producerTemplate = producerTemplate;
        this.collectionGroupMap = collectionGroupMap;
        this.institutionMap = institutionMap;
        this.itemChangeLogDetailsRepository = itemChangeLogDetailsRepository;
        this.collectionGroupDetailsRepository = collectionGroupDetailsRepository;
    }

    @Override
    public Object call() throws Exception {

        long from = pageNum * Long.valueOf(batchSize);
        List<ReportDataEntity> reportDataEntities =  reportDataDetailsRepository.getReportDataEntityForMatchingMonographs(RecapConstants.BIB_ID, from, batchSize);
        List<Integer> nonMonographRecordNums = new ArrayList<>();
        List<Integer> exceptionRecordNums = new ArrayList<>();
        Map<String, List<Integer>> unProcessedRecordNumMap = new HashMap<>();
        for(ReportDataEntity reportDataEntity : reportDataEntities) {
            Map<Integer, Map<Integer, List<ItemEntity>>> useRestrictionMap = new HashMap<>();
            Map<Integer, ItemEntity> itemEntityMap = new HashMap<>();
            String bibId = reportDataEntity.getHeaderValue();
            String[] bibIds = bibId.split(",");
            List<Integer> bibIdList = new ArrayList<>();
            for(int i=0; i< bibIds.length; i++) {
                bibIdList.add(Integer.valueOf(bibIds[i]));
            }
            Set<String> materialTypeSet = new HashSet<>();
            MatchingAlgorithmCGDProcessor matchingAlgorithmCGDProcessor = new MatchingAlgorithmCGDProcessor(bibliographicDetailsRepository, producerTemplate, collectionGroupMap,
                    institutionMap, itemChangeLogDetailsRepository, RecapConstants.INITIAL_MATCHING_OPERATION_TYPE, collectionGroupDetailsRepository);
            boolean isMonograph = matchingAlgorithmCGDProcessor.checkForMonographAndPopulateValues(materialTypeSet,useRestrictionMap, itemEntityMap, bibIdList);
            if(isMonograph) {
                matchingAlgorithmCGDProcessor.updateCGDProcess(useRestrictionMap, itemEntityMap);
            } else {
                if(materialTypeSet.size() > 1) {
                    exceptionRecordNums.add(Integer.valueOf(reportDataEntity.getRecordNum()));
                } else {
                    nonMonographRecordNums.add(Integer.valueOf(reportDataEntity.getRecordNum()));
                }
            }
        }
        if(CollectionUtils.isNotEmpty(nonMonographRecordNums)) {
            unProcessedRecordNumMap.put("NonMonographRecordNums", nonMonographRecordNums);
        }
        if(CollectionUtils.isNotEmpty(exceptionRecordNums)) {
            unProcessedRecordNumMap.put("ExceptionRecordNums", exceptionRecordNums);
        }
        return unProcessedRecordNumMap;
    }
}
