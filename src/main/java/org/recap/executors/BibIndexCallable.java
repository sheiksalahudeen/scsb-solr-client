package org.recap.executors;

import org.apache.commons.lang3.SerializationUtils;
import org.recap.model.*;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.temp.BibCrudRepositoryMultiCoreSupport;
import org.recap.repository.temp.ItemCrudRepositoryMultiCoreSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by pvsubrah on 6/13/16.
 */


public class BibIndexCallable implements Callable {
    private final int from;
    private final int to;
    private String coreName;
    private String solrURL;
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    private BibCrudRepositoryMultiCoreSupport bibCrudRepositoryMultiCoreSupport;

    private ItemCrudRepositoryMultiCoreSupport itemCrudRepositoryMultiCoreSupport;

    public BibIndexCallable(String solrURL, String coreName, int from, int to, BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.coreName = coreName;
        this.solrURL = solrURL;
        this.from = from;
        this.to = to;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
    }

    @Override
    public Object call() throws Exception {

        String threadName = Thread.currentThread().getName();
        System.out.println("Executing thread " + threadName);
        long startTime = System.currentTimeMillis();

        Page<BibliographicEntity> bibliographicEntities = bibliographicDetailsRepository.findAll(new PageRequest(from, to));

        List<Bib> bibsToIndex = new ArrayList<>();
        List<Item> itemsToIndex = new ArrayList<>();

        Iterator<BibliographicEntity> iterator = bibliographicEntities.iterator();


        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Future> futures = new ArrayList<>();
        while(iterator.hasNext()){
            BibliographicEntity bibliographicEntity = iterator.next();

            List<BibliographicHoldingsEntity> bibliographicHoldingsEntities = bibliographicEntity.getBibliographicHoldingsEntities();

            List<HoldingsEntity> holdingsEntities = new ArrayList<>();

            List<ItemEntity> itemEntities = new ArrayList<>();

            for (Iterator<BibliographicHoldingsEntity> bibliographicHoldingsEntityIterator = bibliographicHoldingsEntities.iterator(); bibliographicHoldingsEntityIterator.hasNext(); ) {
                BibliographicHoldingsEntity bibliographicHoldingsEntity = bibliographicHoldingsEntityIterator.next();
                HoldingsEntity holdingsEntity = bibliographicHoldingsEntity.getHoldingsEntity();
                holdingsEntities.add(holdingsEntity);
                for (Iterator<ItemEntity> itemEntityIterator = holdingsEntity.getItemEntities().iterator(); itemEntityIterator.hasNext(); ) {
                    ItemEntity itemEntity = itemEntityIterator.next();
                    itemEntities.add(itemEntity);
                }
            }
            Future submit = executorService.submit(new BibRecordSetupCallable(bibliographicEntity, holdingsEntities, itemEntities));
            futures.add(submit);
        }

        for (Iterator<Future> futureIterator = futures.iterator(); futureIterator.hasNext(); ) {
            Future future = futureIterator.next();

            Map<String, List> stringListMap = (Map<String, List>) future.get();
            List bibs = stringListMap.get("Bib");
            bibsToIndex.addAll(bibs);
            List items = stringListMap.get("Item");
            itemsToIndex.addAll(items);
        }

        executorService.shutdown();


        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to build Bib and related data: " + threadName + " is :" + (endTime-startTime)/1000 + " seconds");

        bibCrudRepositoryMultiCoreSupport = new BibCrudRepositoryMultiCoreSupport(coreName, solrURL);
        if (!CollectionUtils.isEmpty(bibsToIndex)) {
            bibCrudRepositoryMultiCoreSupport.save(bibsToIndex);
        }
        itemCrudRepositoryMultiCoreSupport = new ItemCrudRepositoryMultiCoreSupport(coreName, solrURL);
        if (!CollectionUtils.isEmpty(itemsToIndex)) {
            itemCrudRepositoryMultiCoreSupport.save(itemsToIndex);
        }
        return bibliographicEntities.getSize();
    }
}
