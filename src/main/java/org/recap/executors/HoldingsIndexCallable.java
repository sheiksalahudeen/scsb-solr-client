package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.solr.Holdings;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
public class HoldingsIndexCallable implements Callable {

    Logger logger = LoggerFactory.getLogger(HoldingsIndexCallable.class);

    private final int pageNum;
    private final int docsPerPage;
    private Integer owningInstitutionId;
    private HoldingsDetailsRepository holdingsDetailsRepository;
    private ProducerTemplate producerTemplate;

    public HoldingsIndexCallable(int pageNum, int docsPerPage, HoldingsDetailsRepository holdingsDetailsRepository, Integer owningInstitutionId, ProducerTemplate producerTemplate) {
        this.pageNum = pageNum;
        this.docsPerPage = docsPerPage;
        this.holdingsDetailsRepository = holdingsDetailsRepository;
        this.owningInstitutionId = owningInstitutionId;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Object call() throws Exception {

        Page<HoldingsEntity> holdingsEntities = owningInstitutionId == null ?
                holdingsDetailsRepository.findAll(new PageRequest(pageNum, docsPerPage)) :
                holdingsDetailsRepository.findByOwningInstitutionId(new PageRequest(pageNum, docsPerPage), owningInstitutionId);

        logger.info("Num Holdings Fetched : " + holdingsEntities.getNumberOfElements());
        List<Holdings> holdingsToIndex = new ArrayList<>();

        Iterator<HoldingsEntity> iterator = holdingsEntities.iterator();

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Future> futures = new ArrayList<>();
        while (iterator.hasNext()) {
            HoldingsEntity holdingsEntity = iterator.next();
            Future submit = executorService.submit(new HoldingsRecordSetupCallable(holdingsEntity));
            futures.add(submit);
        }

        for (Iterator<Future> futureIterator = futures.iterator(); futureIterator.hasNext(); ) {
            Future future = futureIterator.next();

            Holdings holdings = (Holdings) future.get();
            holdingsToIndex.add(holdings);
        }

        executorService.shutdown();

        logger.info("No of Holdings to index : " + holdingsToIndex.size());

        if (!CollectionUtils.isEmpty(holdingsToIndex)) {
            producerTemplate.sendBody("seda:solrQ", holdingsToIndex);
        }
        return holdingsEntities.getNumberOfElements();
    }
}
