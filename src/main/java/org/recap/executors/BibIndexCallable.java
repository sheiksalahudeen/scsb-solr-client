package org.recap.executors;

import org.recap.model.*;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.temp.BibCrudRepositoryMultiCoreSupport;
import org.recap.repository.temp.ItemCrudRepositoryMultiCoreSupport;
import org.recap.util.BibJSONUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

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
        while(iterator.hasNext()){
            BibliographicEntity bibliographicEntity = iterator.next();
            Map<String, List> stringListMap = new BibJSONUtil().generateBibAndItemsForIndex(bibliographicEntity);
            bibsToIndex.addAll(stringListMap.get("Bib"));
            itemsToIndex.addAll((stringListMap.get("Item")));
        }


        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to build Bib and related data: " + threadName + " is :" + (endTime-startTime)/1000 + " milli seconds");

        bibCrudRepositoryMultiCoreSupport = new BibCrudRepositoryMultiCoreSupport(coreName, solrURL);
        if (!CollectionUtils.isEmpty(bibsToIndex)) {
            bibCrudRepositoryMultiCoreSupport.save(bibsToIndex);
        }
//        itemCrudRepositoryMultiCoreSupport = new ItemCrudRepositoryMultiCoreSupport(coreName, solrURL);
//        if (!CollectionUtils.isEmpty(itemsToIndex)) {
//            itemCrudRepositoryMultiCoreSupport.save(itemsToIndex);
//        }
        return null;
    }
}
