package org.recap.matchingalgorithm.report;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.MatchingCounter;
import org.recap.matchingalgorithm.service.MatchingAlgorithmHelperService;
import org.recap.matchingalgorithm.service.MatchingAlgorithmUpdateCGDService;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.ReportDataDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by angelind on 9/12/16.
 */
public class MatchingAlgoReportUT extends BaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(MatchingAlgoReportUT.class);

    @Autowired
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    MatchingAlgorithmUpdateCGDService matchingAlgorithmUpdateCGDService;

    @Mock
    ReportDataDetailsRepository reportDataDetailsRepository;

    private Integer batchSize=10000;
    private Map collectionGroupMap = new HashMap();

    @Test
    public void isItemCgdInSolrSyncWithDB() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 1000;
        Integer count = 0;
        logger.info("Item Ids which are not Updated : ");
        String operationType = RecapConstants.INITIAL_MATCHING_OPERATION_TYPE;
        Page<Integer> recordIdList = itemChangeLogDetailsRepository.getRecordIdByOperationType(new PageRequest(0, batchSize), operationType);
        int totalPages = recordIdList.getTotalPages();
        List<Integer> recordIds = recordIdList.getContent();
        List<ItemEntity> itemEntities = itemDetailsRepository.findByItemIdIn(recordIds);
        count = count + compareItemsFromDBToSolr(itemEntities);
        for(int i=1; i<totalPages; i++) {
            recordIdList = itemChangeLogDetailsRepository.getRecordIdByOperationType(new PageRequest(i, batchSize), operationType);
            recordIds = recordIdList.getContent();
            itemEntities = itemDetailsRepository.findByItemIdIn(recordIds);
            count = count + compareItemsFromDBToSolr(itemEntities);
        }
        logger.info("Total Item Count not updated : " + count);
        stopWatch.stop();
        logger.info("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
        assertEquals(Integer.valueOf(0), count);
    }

    @Test
    public void getMatchingItemCountsForSerial() throws Exception {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        MatchingCounter.reset();
        Mockito.when(matchingAlgorithmUpdateCGDService.getReportDataDetailsRepository()).thenReturn(reportDataDetailsRepository);
        Mockito.when(reportDataDetailsRepository.getCountOfRecordNumForMatchingSerials(RecapConstants.BIB_ID)).thenReturn(new Long(0));
        Mockito.doCallRealMethod().when(matchingAlgorithmUpdateCGDService).getItemsCountForSerialsMatching(batchSize);
        MatchingCounter.updateCounter(1, false);
        MatchingCounter.updateCounter(2, false);
        MatchingCounter.updateCounter(3, false);
        matchingAlgorithmUpdateCGDService.getItemsCountForSerialsMatching(batchSize);
        assertTrue(MatchingCounter.getPulCGDUpdatedSharedCount() > 0);
        assertTrue(MatchingCounter.getCulCGDUpdatedSharedCount() > 0);
        assertTrue(MatchingCounter.getNyplCGDUpdatedSharedCount() > 0);
        logger.info("Total PUL Shared Serial Items in Matching : " + MatchingCounter.getPulCGDUpdatedSharedCount());
        logger.info("Total CUL Shared Serial Items in Matching : " + MatchingCounter.getCulCGDUpdatedSharedCount());
        logger.info("Total NYPL Shared Serial Items in Matching : " + MatchingCounter.getNyplCGDUpdatedSharedCount());
        stopwatch.stop();
        logger.info("Total Time taken to get the serial items count : " + stopwatch.getTotalTimeSeconds() + " seconds");
    }

    private Integer compareItemsFromDBToSolr(List<ItemEntity> itemEntities) {
        Integer count = 0;
        for(ItemEntity itemEntity : itemEntities) {
            Item item = itemCrudRepository.findByItemId(itemEntity.getItemId());
            if(!(item.getCollectionGroupDesignation().equalsIgnoreCase((String) getCollectionGroupMap().get(itemEntity.getCollectionGroupId())))) {
                count++;
                logger.info("" + itemEntity.getItemId());
            }
        }
        return count;
    }

    public Map getCollectionGroupMap() {
        if (null == collectionGroupMap || collectionGroupMap.size() == 0) {
            collectionGroupMap = new HashMap();
            Iterable<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
            for (Iterator<CollectionGroupEntity> iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                CollectionGroupEntity collectionGroupEntity = iterator.next();
                collectionGroupMap.put(collectionGroupEntity.getCollectionGroupId(), collectionGroupEntity.getCollectionGroupCode());
            }
        }
        return collectionGroupMap;
    }
}
