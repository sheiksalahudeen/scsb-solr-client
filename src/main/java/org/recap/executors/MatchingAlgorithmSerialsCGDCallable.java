package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.MatchingAlgorithmCGDProcessor;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.repository.jpa.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by angelind on 31/5/17.
 */
public class MatchingAlgorithmSerialsCGDCallable implements Callable {

    private ReportDataDetailsRepository reportDataDetailsRepository;
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private int pageNum;
    private Integer batchSize;
    private ProducerTemplate producerTemplate;
    private Map collectionGroupMap;
    private Map institutionMap;
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;
    private ItemDetailsRepository itemDetailsRepository;

    public MatchingAlgorithmSerialsCGDCallable(ReportDataDetailsRepository reportDataDetailsRepository, BibliographicDetailsRepository bibliographicDetailsRepository, int pageNum, Integer batchSize,
                                               ProducerTemplate producerTemplate, Map collectionGroupMap, Map institutionMap, ItemChangeLogDetailsRepository itemChangeLogDetailsRepository,
                                               CollectionGroupDetailsRepository collectionGroupDetailsRepository, ItemDetailsRepository itemDetailsRepository) {
        this.reportDataDetailsRepository = reportDataDetailsRepository;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.pageNum = pageNum;
        this.batchSize = batchSize;
        this.producerTemplate = producerTemplate;
        this.collectionGroupMap = collectionGroupMap;
        this.institutionMap = institutionMap;
        this.itemChangeLogDetailsRepository = itemChangeLogDetailsRepository;
        this.collectionGroupDetailsRepository = collectionGroupDetailsRepository;
        this.itemDetailsRepository = itemDetailsRepository;
    }

    @Override
    public Object call() throws Exception {
        long from = pageNum * Long.valueOf(batchSize);
        List<ReportDataEntity> reportDataEntities =  reportDataDetailsRepository.getReportDataEntityForMatchingSerials(RecapConstants.BIB_ID, from, batchSize);
        for(ReportDataEntity reportDataEntity : reportDataEntities) {
            Map<Integer, ItemEntity> itemEntityMap = new HashMap<>();
            String bibId = reportDataEntity.getHeaderValue();
            String[] bibIds = bibId.split(",");
            List<Integer> bibIdList = new ArrayList<>();
            MatchingAlgorithmCGDProcessor matchingAlgorithmCGDProcessor = new MatchingAlgorithmCGDProcessor(bibliographicDetailsRepository, producerTemplate, collectionGroupMap,
                    institutionMap, itemChangeLogDetailsRepository, RecapConstants.INITIAL_MATCHING_OPERATION_TYPE, collectionGroupDetailsRepository, itemDetailsRepository);
            for(int i=0; i< bibIds.length; i++) {
                bibIdList.add(Integer.valueOf(bibIds[i]));
            }
            matchingAlgorithmCGDProcessor.populateItemEntityMap(itemEntityMap, bibIdList);
            matchingAlgorithmCGDProcessor.updateItemsCGD(itemEntityMap);
        }
        return null;
    }
}
